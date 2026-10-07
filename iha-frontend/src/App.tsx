import { useCallback, useEffect, useRef, useState } from 'react';
import { ihaApi } from './api/ihaApi';
import { ApiError } from './api/httpClient';
import { ConfirmDialog } from './components/ConfirmDialog';
import { FilterBar } from './components/FilterBar';
import { IhaFormModal } from './components/IhaFormModal';
import { IhaTable } from './components/IhaTable';
import { Pagination } from './components/Pagination';
import { useDebounce } from './hooks/useDebounce';
import { DURUM_ETIKETLERI, type Iha, type IhaDurum, type IhaFilters, type PageResponse } from './types/iha';

type FormState = { mode: 'closed' } | { mode: 'create' } | { mode: 'edit'; iha: Iha };

interface Toast {
  id: number;
  kind: 'success' | 'error';
  text: string;
}

const INITIAL_FILTERS: IhaFilters = { durum: '', kategori: '', model: '' };

function errorMessage(err: unknown): string {
  return err instanceof ApiError ? err.message : 'Beklenmeyen bir hata oluştu';
}

export default function App() {
  const [filters, setFilters] = useState<IhaFilters>(INITIAL_FILTERS);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sort, setSort] = useState('id,asc');
  const [data, setData] = useState<PageResponse<Iha> | null>(null);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [reloadKey, setReloadKey] = useState(0);

  const [form, setForm] = useState<FormState>({ mode: 'closed' });
  const [deleting, setDeleting] = useState<Iha | null>(null);
  const [deleteBusy, setDeleteBusy] = useState(false);
  const [busyId, setBusyId] = useState<number | null>(null);
  const [toasts, setToasts] = useState<Toast[]>([]);
  const toastSeq = useRef(0);

  const debouncedModel = useDebounce(filters.model, 350);

  const notify = useCallback((kind: Toast['kind'], text: string) => {
    const id = ++toastSeq.current;
    setToasts((prev) => [...prev, { id, kind, text }]);
    setTimeout(() => setToasts((prev) => prev.filter((t) => t.id !== id)), 4000);
  }, []);

  const reload = () => setReloadKey((k) => k + 1);

  useEffect(() => {
    // Hızlı filtre değişimlerinde eski cevapların yenisini ezmesini önler.
    let cancelled = false;
    setLoading(true);
    ihaApi
      .list({ page, size, sort, durum: filters.durum, kategori: filters.kategori, model: debouncedModel })
      .then((result) => {
        if (cancelled) return;
        // Son sayfadaki son kayıt silindiyse bir önceki sayfaya geç.
        if (result.content.length === 0 && result.page > 0 && result.totalPages > 0) {
          setPage(result.totalPages - 1);
          return;
        }
        setData(result);
        setLoadError(null);
      })
      .catch((err) => {
        if (!cancelled) setLoadError(errorMessage(err));
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [page, size, sort, filters.durum, filters.kategori, debouncedModel, reloadKey]);

  const handleFiltersChange = (next: IhaFilters) => {
    setFilters(next);
    setPage(0);
  };

  const handleDurumChange = async (iha: Iha, durum: IhaDurum) => {
    setBusyId(iha.id);
    try {
      const updated = await ihaApi.updateDurum(iha.id, durum);
      setData((prev) =>
        prev && { ...prev, content: prev.content.map((x) => (x.id === updated.id ? updated : x)) },
      );
      notify('success', `${iha.seriNo} durumu "${DURUM_ETIKETLERI[durum]}" olarak güncellendi`);
      // Durum filtresi aktifse kayıt artık listeye uymayabilir.
      if (filters.durum) reload();
    } catch (err) {
      notify('error', errorMessage(err));
    } finally {
      setBusyId(null);
    }
  };

  const handleDelete = async () => {
    if (!deleting) return;
    setDeleteBusy(true);
    try {
      await ihaApi.remove(deleting.id);
      notify('success', `${deleting.seriNo} silindi`);
      setDeleting(null);
      reload();
    } catch (err) {
      notify('error', errorMessage(err));
    } finally {
      setDeleteBusy(false);
    }
  };

  const handleSaved = (iha: Iha, created: boolean) => {
    setForm({ mode: 'closed' });
    notify('success', created ? `${iha.seriNo} kaydedildi` : `${iha.seriNo} güncellendi`);
    reload();
  };

  const closeForm = useCallback(() => setForm({ mode: 'closed' }), []);

  return (
    <div className="app">
      <header className="app-header">
        <div>
          <h1>İHA Kayıt Yönetim Sistemi</h1>
          <p className="muted">İnsansız hava aracı envanterini görüntüleyin, ekleyin ve güncelleyin.</p>
        </div>
        <button type="button" className="btn btn-primary" onClick={() => setForm({ mode: 'create' })}>
          + Yeni İHA
        </button>
      </header>

      <main className="card">
        <FilterBar filters={filters} onChange={handleFiltersChange} />

        {loadError && (
          <div className="alert alert-error">
            {loadError}
            <button type="button" className="btn btn-small" onClick={reload}>Tekrar dene</button>
          </div>
        )}

        <div className={loading && data ? 'content loading' : 'content'}>
          {data ? (
            <>
              <IhaTable
                items={data.content}
                sort={sort}
                busyId={busyId}
                onSortChange={(s) => {
                  setSort(s);
                  setPage(0);
                }}
                onEdit={(iha) => setForm({ mode: 'edit', iha })}
                onDelete={setDeleting}
                onDurumChange={handleDurumChange}
              />
              <Pagination
                page={data.page}
                totalPages={data.totalPages}
                totalElements={data.totalElements}
                size={size}
                onPageChange={setPage}
                onSizeChange={(s) => {
                  setSize(s);
                  setPage(0);
                }}
              />
            </>
          ) : (
            loading && <p className="empty">Yükleniyor…</p>
          )}
        </div>
      </main>

      {form.mode !== 'closed' && (
        <IhaFormModal
          key={form.mode === 'edit' ? form.iha.id : 'new'}
          editing={form.mode === 'edit' ? form.iha : null}
          onClose={closeForm}
          onSaved={handleSaved}
        />
      )}

      {deleting && (
        <ConfirmDialog
          title="İHA kaydını sil"
          message={`${deleting.seriNo} seri numaralı "${deleting.model}" kaydı kalıcı olarak silinecek. Emin misiniz?`}
          confirmLabel="Sil"
          busy={deleteBusy}
          onConfirm={handleDelete}
          onCancel={() => setDeleting(null)}
        />
      )}

      <div className="toasts" aria-live="polite">
        {toasts.map((t) => (
          <div key={t.id} className={`toast toast-${t.kind}`}>{t.text}</div>
        ))}
      </div>
    </div>
  );
}
