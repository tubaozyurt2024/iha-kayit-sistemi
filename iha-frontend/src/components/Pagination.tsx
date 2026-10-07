interface Props {
  page: number;
  totalPages: number;
  totalElements: number;
  size: number;
  onPageChange: (page: number) => void;
  onSizeChange: (size: number) => void;
}

const SIZE_OPTIONS = [5, 10, 20, 50];

export function Pagination({ page, totalPages, totalElements, size, onPageChange, onSizeChange }: Props) {
  const from = totalElements === 0 ? 0 : page * size + 1;
  const to = Math.min((page + 1) * size, totalElements);

  return (
    <div className="pagination">
      <span className="muted">
        {from}–{to} / toplam {totalElements} kayıt
      </span>
      <div className="pagination-controls">
        <label>
          Sayfa başına
          <select value={size} onChange={(e) => onSizeChange(Number(e.target.value))}>
            {SIZE_OPTIONS.map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>
        </label>
        <button type="button" className="btn" disabled={page <= 0} onClick={() => onPageChange(page - 1)}>
          ‹ Önceki
        </button>
        <span>
          Sayfa {totalPages === 0 ? 0 : page + 1} / {totalPages}
        </span>
        <button
          type="button"
          className="btn"
          disabled={page + 1 >= totalPages}
          onClick={() => onPageChange(page + 1)}
        >
          Sonraki ›
        </button>
      </div>
    </div>
  );
}
