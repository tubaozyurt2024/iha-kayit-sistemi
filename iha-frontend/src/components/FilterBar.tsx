import {
  DURUM_ETIKETLERI,
  IHA_DURUMLARI,
  IHA_KATEGORILERI,
  KATEGORI_ETIKETLERI,
  type IhaDurum,
  type IhaFilters,
  type IhaKategori,
} from '../types/iha';

interface Props {
  filters: IhaFilters;
  onChange: (filters: IhaFilters) => void;
}

export function FilterBar({ filters, onChange }: Props) {
  const hasFilter = filters.durum !== '' || filters.kategori !== '' || filters.model !== '';

  return (
    <div className="filter-bar">
      <label>
        Model
        <input
          type="search"
          placeholder="Model adında ara…"
          value={filters.model}
          onChange={(e) => onChange({ ...filters, model: e.target.value })}
        />
      </label>
      <label>
        Kategori
        <select
          value={filters.kategori}
          onChange={(e) => onChange({ ...filters, kategori: e.target.value as IhaKategori | '' })}
        >
          <option value="">Tümü</option>
          {IHA_KATEGORILERI.map((k) => (
            <option key={k} value={k}>{KATEGORI_ETIKETLERI[k]}</option>
          ))}
        </select>
      </label>
      <label>
        Durum
        <select
          value={filters.durum}
          onChange={(e) => onChange({ ...filters, durum: e.target.value as IhaDurum | '' })}
        >
          <option value="">Tümü</option>
          {IHA_DURUMLARI.map((d) => (
            <option key={d} value={d}>{DURUM_ETIKETLERI[d]}</option>
          ))}
        </select>
      </label>
      {hasFilter && (
        <button
          type="button"
          className="btn btn-ghost"
          onClick={() => onChange({ durum: '', kategori: '', model: '' })}
        >
          Filtreleri temizle
        </button>
      )}
    </div>
  );
}
