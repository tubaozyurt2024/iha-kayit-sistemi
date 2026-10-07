export const IHA_KATEGORILERI = ['MINI', 'TAKTIK', 'MALE', 'HALE'] as const;
export const IHA_DURUMLARI = ['AKTIF', 'BAKIMDA', 'PASIF', 'HURDA'] as const;

export type IhaKategori = (typeof IHA_KATEGORILERI)[number];
export type IhaDurum = (typeof IHA_DURUMLARI)[number];

export const KATEGORI_ETIKETLERI: Record<IhaKategori, string> = {
  MINI: 'Mini',
  TAKTIK: 'Taktik',
  MALE: 'MALE',
  HALE: 'HALE',
};

export const DURUM_ETIKETLERI: Record<IhaDurum, string> = {
  AKTIF: 'Aktif',
  BAKIMDA: 'Bakımda',
  PASIF: 'Pasif',
  HURDA: 'Hurda',
};

export interface Iha {
  id: number;
  seriNo: string;
  model: string;
  uretici: string;
  kategori: IhaKategori;
  durum: IhaDurum;
  maksIrtifaMetre: number;
  menzilKm: number;
  havadaKalmaSuresiSaat: number;
  azamiKalkisAgirligiKg: number;
  uretimTarihi: string;
  sonBakimTarihi: string | null;
  createdAt: string;
  updatedAt: string;
}

/** Create ve PUT istekleri aynı gövdeyi kullanır. */
export interface IhaRequest {
  seriNo: string;
  model: string;
  uretici: string;
  kategori: IhaKategori;
  durum: IhaDurum;
  maksIrtifaMetre: number;
  menzilKm: number;
  havadaKalmaSuresiSaat: number;
  azamiKalkisAgirligiKg: number;
  uretimTarihi: string;
  sonBakimTarihi: string | null;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface IhaFilters {
  durum: IhaDurum | '';
  kategori: IhaKategori | '';
  model: string;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path?: string;
  validationErrors?: Record<string, string>;
}
