import {
  DURUM_ETIKETLERI,
  IHA_DURUMLARI,
  KATEGORI_ETIKETLERI,
  type Iha,
  type IhaDurum,
} from '../types/iha';

interface Props {
  items: Iha[];
  sort: string;
  busyId: number | null;
  onSortChange: (sort: string) => void;
  onEdit: (iha: Iha) => void;
  onDelete: (iha: Iha) => void;
  onDurumChange: (iha: Iha, durum: IhaDurum) => void;
}

interface Column {
  key: string;
  label: string;
  numeric?: boolean;
}

const COLUMNS: Column[] = [
  { key: 'seriNo', label: 'Seri No' },
  { key: 'model', label: 'Model' },
  { key: 'uretici', label: 'Üretici' },
  { key: 'kategori', label: 'Kategori' },
  { key: 'durum', label: 'Durum' },
  { key: 'maksIrtifaMetre', label: 'İrtifa (m)', numeric: true },
  { key: 'menzilKm', label: 'Menzil (km)', numeric: true },
  { key: 'havadaKalmaSuresiSaat', label: 'Süre (sa)', numeric: true },
  { key: 'azamiKalkisAgirligiKg', label: 'MTOW (kg)', numeric: true },
  { key: 'uretimTarihi', label: 'Üretim' },
  { key: 'sonBakimTarihi', label: 'Son Bakım' },
];

const numberFormat = new Intl.NumberFormat('tr-TR', { maximumFractionDigits: 2 });

function formatDate(iso: string | null): string {
  if (!iso) return '—';
  const [y, m, d] = iso.split('-');
  return `${d}.${m}.${y}`;
}

export function IhaTable({ items, sort, busyId, onSortChange, onEdit, onDelete, onDurumChange }: Props) {
  const [sortField, sortDir] = sort.split(',');

  const toggleSort = (key: string) => {
    const nextDir = sortField === key && sortDir === 'asc' ? 'desc' : 'asc';
    onSortChange(`${key},${nextDir}`);
  };

  if (items.length === 0) {
    return <p className="empty">Kriterlere uyan İHA kaydı bulunamadı.</p>;
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {COLUMNS.map((col) => {
              const active = sortField === col.key;
              return (
                <th
                  key={col.key}
                  className={col.numeric ? 'num' : undefined}
                  aria-sort={active ? (sortDir === 'asc' ? 'ascending' : 'descending') : 'none'}
                >
                  <button type="button" className="sort-btn" onClick={() => toggleSort(col.key)}>
                    {col.label}
                    <span className="sort-icon">{active ? (sortDir === 'asc' ? '▲' : '▼') : '↕'}</span>
                  </button>
                </th>
              );
            })}
            <th aria-label="İşlemler" />
          </tr>
        </thead>
        <tbody>
          {items.map((iha) => {
            const hurda = iha.durum === 'HURDA';
            const busy = busyId === iha.id;
            return (
              <tr key={iha.id} className={busy ? 'busy' : undefined}>
                <td className="mono">{iha.seriNo}</td>
                <td>{iha.model}</td>
                <td>{iha.uretici}</td>
                <td>{KATEGORI_ETIKETLERI[iha.kategori]}</td>
                <td>
                  <select
                    className={`durum durum-${iha.durum.toLowerCase()}`}
                    value={iha.durum}
                    disabled={hurda || busy}
                    title={hurda ? 'HURDA durumundaki İHA\'nın durumu değiştirilemez' : undefined}
                    onChange={(e) => onDurumChange(iha, e.target.value as IhaDurum)}
                  >
                    {IHA_DURUMLARI.map((d) => (
                      <option key={d} value={d}>{DURUM_ETIKETLERI[d]}</option>
                    ))}
                  </select>
                </td>
                <td className="num">{numberFormat.format(iha.maksIrtifaMetre)}</td>
                <td className="num">{numberFormat.format(iha.menzilKm)}</td>
                <td className="num">{numberFormat.format(iha.havadaKalmaSuresiSaat)}</td>
                <td className="num">{numberFormat.format(iha.azamiKalkisAgirligiKg)}</td>
                <td>{formatDate(iha.uretimTarihi)}</td>
                <td>{formatDate(iha.sonBakimTarihi)}</td>
                <td className="actions">
                  <button type="button" className="btn btn-small" disabled={busy} onClick={() => onEdit(iha)}>
                    Düzenle
                  </button>
                  <button
                    type="button"
                    className="btn btn-small btn-danger"
                    disabled={busy}
                    onClick={() => onDelete(iha)}
                  >
                    Sil
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
