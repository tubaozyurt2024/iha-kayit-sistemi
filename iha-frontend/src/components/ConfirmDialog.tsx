import { Modal } from './Modal';

interface Props {
  title: string;
  message: string;
  confirmLabel: string;
  busy: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

export function ConfirmDialog({ title, message, confirmLabel, busy, onConfirm, onCancel }: Props) {
  return (
    <Modal title={title} onClose={busy ? () => {} : onCancel}>
      <p>{message}</p>
      <footer className="modal-footer">
        <button type="button" className="btn" disabled={busy} onClick={onCancel}>
          Vazgeç
        </button>
        <button type="button" className="btn btn-danger" disabled={busy} onClick={onConfirm}>
          {busy ? 'Siliniyor…' : confirmLabel}
        </button>
      </footer>
    </Modal>
  );
}
