import { useState, type FormEvent } from 'react';
import { ihaApi } from '../api/ihaApi';
import { ApiError } from '../api/httpClient';
import {
  DURUM_ETIKETLERI,
  IHA_DURUMLARI,
  IHA_KATEGORILERI,
  KATEGORI_ETIKETLERI,
  type Iha,
  type IhaDurum,
  type IhaKategori,
} from '../types/iha';
import {
  EMPTY_FORM,
  toFormValues,
  toRequest,
  todayIso,
  validate,
  type FormErrors,
  type IhaFormValues,
} from '../utils/validation';
import { Modal } from './Modal';

interface Props {
  /** null ise yeni kayıt oluşturulur. */
  editing: Iha | null;
  onClose: () => void;
  onSaved: (iha: Iha, created: boolean) => void;
}

export function IhaFormModal({ editing, onClose, onSaved }: Props) {
  const [values, setValues] = useState<IhaFormValues>(editing ? toFormValues(editing) : EMPTY_FORM);
  const [errors, setErrors] = useState<FormErrors>({});
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const today = todayIso();
  const durumKilitli = editing?.durum === 'HURDA';

  const set = <K extends keyof IhaFormValues>(key: K, value: IhaFormValues[K]) => {
    setValues((prev) => ({ ...prev, [key]: value }));
    setErrors((prev) => ({ ...prev, [key]: undefined }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    const clientErrors = validate(values, today);
    setErrors(clientErrors);
    setSubmitError(null);
    if (Object.keys(clientErrors).length > 0) return;

    setSubmitting(true);
    try {
      const body = toRequest(values);
      const saved = editing ? await ihaApi.update(editing.id, body) : await ihaApi.create(body);
      onSaved(saved, !editing);
    } catch (err) {
      if (err instanceof ApiError) {
        setErrors(err.validationErrors as FormErrors);
        setSubmitError(err.message);
      } else {
        setSubmitError('Beklenmeyen bir hata oluştu');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const field = (key: keyof IhaFormValues) => ({
    id: key,
    'aria-invalid': errors[key] ? true : undefined,
    'aria-describedby': errors[key] ? `${key}-error` : undefined,
  });

  const errorOf = (key: keyof IhaFormValues) =>
    errors[key] && <span id={`${key}-error`} className="field-error">{errors[key]}</span>;

  return (
    <Modal title={editing ? `İHA Düzenle — ${editing.seriNo}` : 'Yeni İHA Kaydı'} onClose={onClose}>
      <form className="iha-form" onSubmit={handleSubmit} noValidate>
        {submitError && <div className="alert alert-error">{submitError}</div>}

        <div className="form-grid">
          <div className="form-field">
            <label htmlFor="seriNo">Seri No *</label>
            <input {...field('seriNo')} maxLength={50} value={values.seriNo}
              onChange={(e) => set('seriNo', e.target.value)} autoFocus />
            {errorOf('seriNo')}
          </div>
          <div className="form-field">
            <label htmlFor="model">Model *</label>
            <input {...field('model')} maxLength={100} value={values.model}
              onChange={(e) => set('model', e.target.value)} />
            {errorOf('model')}
          </div>
          <div className="form-field">
            <label htmlFor="uretici">Üretici *</label>
            <input {...field('uretici')} maxLength={100} value={values.uretici}
              onChange={(e) => set('uretici', e.target.value)} />
            {errorOf('uretici')}
          </div>
          <div className="form-field">
            <label htmlFor="kategori">Kategori *</label>
            <select {...field('kategori')} value={values.kategori}
              onChange={(e) => set('kategori', e.target.value as IhaKategori)}>
              {IHA_KATEGORILERI.map((k) => (
                <option key={k} value={k}>{KATEGORI_ETIKETLERI[k]}</option>
              ))}
            </select>
            {errorOf('kategori')}
          </div>
          <div className="form-field">
            <label htmlFor="durum">Durum *</label>
            <select {...field('durum')} value={values.durum} disabled={durumKilitli}
              onChange={(e) => set('durum', e.target.value as IhaDurum)}>
              {IHA_DURUMLARI.map((d) => (
                <option key={d} value={d}>{DURUM_ETIKETLERI[d]}</option>
              ))}
            </select>
            {durumKilitli && <span className="field-hint">HURDA durumundaki İHA'nın durumu değiştirilemez</span>}
            {errorOf('durum')}
          </div>
          <div className="form-field">
            <label htmlFor="maksIrtifaMetre">Maks. İrtifa (m) *</label>
            <input {...field('maksIrtifaMetre')} type="number" min={1} step={1} inputMode="numeric"
              value={values.maksIrtifaMetre} onChange={(e) => set('maksIrtifaMetre', e.target.value)} />
            {errorOf('maksIrtifaMetre')}
          </div>
          <div className="form-field">
            <label htmlFor="menzilKm">Menzil (km) *</label>
            <input {...field('menzilKm')} type="number" min={0} step="any" inputMode="decimal"
              value={values.menzilKm} onChange={(e) => set('menzilKm', e.target.value)} />
            {errorOf('menzilKm')}
          </div>
          <div className="form-field">
            <label htmlFor="havadaKalmaSuresiSaat">Havada Kalma (saat) *</label>
            <input {...field('havadaKalmaSuresiSaat')} type="number" min={0} step="any" inputMode="decimal"
              value={values.havadaKalmaSuresiSaat} onChange={(e) => set('havadaKalmaSuresiSaat', e.target.value)} />
            {errorOf('havadaKalmaSuresiSaat')}
          </div>
          <div className="form-field">
            <label htmlFor="azamiKalkisAgirligiKg">Azami Kalkış Ağırlığı (kg) *</label>
            <input {...field('azamiKalkisAgirligiKg')} type="number" min={0} step="any" inputMode="decimal"
              value={values.azamiKalkisAgirligiKg} onChange={(e) => set('azamiKalkisAgirligiKg', e.target.value)} />
            {errorOf('azamiKalkisAgirligiKg')}
          </div>
          <div className="form-field">
            <label htmlFor="uretimTarihi">Üretim Tarihi *</label>
            <input {...field('uretimTarihi')} type="date" max={today}
              value={values.uretimTarihi} onChange={(e) => set('uretimTarihi', e.target.value)} />
            {errorOf('uretimTarihi')}
          </div>
          <div className="form-field">
            <label htmlFor="sonBakimTarihi">Son Bakım Tarihi</label>
            <input {...field('sonBakimTarihi')} type="date" min={values.uretimTarihi || undefined} max={today}
              value={values.sonBakimTarihi} onChange={(e) => set('sonBakimTarihi', e.target.value)} />
            {errorOf('sonBakimTarihi')}
          </div>
        </div>

        <footer className="modal-footer">
          <button type="button" className="btn" onClick={onClose} disabled={submitting}>
            Vazgeç
          </button>
          <button type="submit" className="btn btn-primary" disabled={submitting}>
            {submitting ? 'Kaydediliyor…' : editing ? 'Güncelle' : 'Kaydet'}
          </button>
        </footer>
      </form>
    </Modal>
  );
}
