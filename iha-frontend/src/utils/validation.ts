import type { Iha, IhaDurum, IhaKategori, IhaRequest } from '../types/iha';

/** Form alanları input'lardan string olarak gelir; sayıya çevirme gönderimde yapılır. */
export interface IhaFormValues {
  seriNo: string;
  model: string;
  uretici: string;
  kategori: IhaKategori;
  durum: IhaDurum;
  maksIrtifaMetre: string;
  menzilKm: string;
  havadaKalmaSuresiSaat: string;
  azamiKalkisAgirligiKg: string;
  uretimTarihi: string;
  sonBakimTarihi: string;
}

export type FormErrors = Partial<Record<keyof IhaFormValues, string>>;

export const EMPTY_FORM: IhaFormValues = {
  seriNo: '',
  model: '',
  uretici: '',
  kategori: 'MINI',
  durum: 'AKTIF',
  maksIrtifaMetre: '',
  menzilKm: '',
  havadaKalmaSuresiSaat: '',
  azamiKalkisAgirligiKg: '',
  uretimTarihi: '',
  sonBakimTarihi: '',
};

/** Yerel saat dilimine göre bugünün tarihi (YYYY-MM-DD); toISOString UTC'ye kaydırır. */
export function todayIso(now: Date = new Date()): string {
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`;
}

export function toFormValues(iha: Iha): IhaFormValues {
  return {
    seriNo: iha.seriNo,
    model: iha.model,
    uretici: iha.uretici,
    kategori: iha.kategori,
    durum: iha.durum,
    maksIrtifaMetre: String(iha.maksIrtifaMetre),
    menzilKm: String(iha.menzilKm),
    havadaKalmaSuresiSaat: String(iha.havadaKalmaSuresiSaat),
    azamiKalkisAgirligiKg: String(iha.azamiKalkisAgirligiKg),
    uretimTarihi: iha.uretimTarihi,
    sonBakimTarihi: iha.sonBakimTarihi ?? '',
  };
}

function requiredText(value: string, label: string, max: number): string | undefined {
  const trimmed = value.trim();
  if (!trimmed) return `${label} zorunludur`;
  if (trimmed.length > max) return `${label} en fazla ${max} karakter olabilir`;
  return undefined;
}

function positiveNumber(value: string, label: string, integer = false): string | undefined {
  if (!value.trim()) return `${label} zorunludur`;
  const n = Number(value);
  if (!Number.isFinite(n)) return `${label} geçerli bir sayı olmalıdır`;
  if (integer && !Number.isInteger(n)) return `${label} tam sayı olmalıdır`;
  if (n <= 0) return `${label} pozitif olmalıdır`;
  return undefined;
}

/** Backend'deki Bean Validation ve iş kurallarının istemci tarafı karşılığı. */
export function validate(values: IhaFormValues, today: string = todayIso()): FormErrors {
  const errors: FormErrors = {
    seriNo: requiredText(values.seriNo, 'Seri numarası', 50),
    model: requiredText(values.model, 'Model', 100),
    uretici: requiredText(values.uretici, 'Üretici', 100),
    maksIrtifaMetre: positiveNumber(values.maksIrtifaMetre, 'Maksimum irtifa', true),
    menzilKm: positiveNumber(values.menzilKm, 'Menzil'),
    havadaKalmaSuresiSaat: positiveNumber(values.havadaKalmaSuresiSaat, 'Havada kalma süresi'),
    azamiKalkisAgirligiKg: positiveNumber(values.azamiKalkisAgirligiKg, 'Azami kalkış ağırlığı'),
  };

  // YYYY-MM-DD formatındaki tarihler string olarak doğru sıralanır.
  if (!values.uretimTarihi) {
    errors.uretimTarihi = 'Üretim tarihi zorunludur';
  } else if (values.uretimTarihi > today) {
    errors.uretimTarihi = 'Üretim tarihi gelecekte olamaz';
  }

  if (values.sonBakimTarihi) {
    if (values.sonBakimTarihi > today) {
      errors.sonBakimTarihi = 'Son bakım tarihi gelecekte olamaz';
    } else if (values.uretimTarihi && values.sonBakimTarihi < values.uretimTarihi) {
      errors.sonBakimTarihi = 'Son bakım tarihi, üretim tarihinden önce olamaz';
    }
  }

  return Object.fromEntries(
    Object.entries(errors).filter(([, message]) => message !== undefined),
  ) as FormErrors;
}

export function toRequest(values: IhaFormValues): IhaRequest {
  return {
    seriNo: values.seriNo.trim(),
    model: values.model.trim(),
    uretici: values.uretici.trim(),
    kategori: values.kategori,
    durum: values.durum,
    maksIrtifaMetre: Number(values.maksIrtifaMetre),
    menzilKm: Number(values.menzilKm),
    havadaKalmaSuresiSaat: Number(values.havadaKalmaSuresiSaat),
    azamiKalkisAgirligiKg: Number(values.azamiKalkisAgirligiKg),
    uretimTarihi: values.uretimTarihi,
    sonBakimTarihi: values.sonBakimTarihi || null,
  };
}
