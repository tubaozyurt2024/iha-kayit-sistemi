import type { ErrorResponse } from '../types/iha';

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1';

/** Backend'in tek tip hata cevabını taşıyan hata sınıfı. */
export class ApiError extends Error {
  readonly status: number;
  readonly validationErrors: Record<string, string>;

  constructor(status: number, message: string, validationErrors: Record<string, string> = {}) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.validationErrors = validationErrors;
  }
}

export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  let response: Response;
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      ...init,
      headers: {
        Accept: 'application/json',
        ...(init.body ? { 'Content-Type': 'application/json' } : {}),
        ...init.headers,
      },
    });
  } catch {
    throw new ApiError(0, 'Sunucuya ulaşılamadı. Backend çalışıyor mu? (http://localhost:8080)');
  }

  if (!response.ok) {
    const body = await response.json().catch(() => null) as ErrorResponse | null;
    throw new ApiError(
      response.status,
      body?.message ?? `İstek başarısız (${response.status})`,
      body?.validationErrors ?? {},
    );
  }

  if (response.status === 204) {
    return undefined as T;
  }
  return response.json() as Promise<T>;
}
