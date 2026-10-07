import { request } from './httpClient';
import type { Iha, IhaDurum, IhaFilters, IhaRequest, PageResponse } from '../types/iha';

const RESOURCE = '/ihalar';

export interface ListParams extends IhaFilters {
  page: number;
  size: number;
  sort: string;
}

export const ihaApi = {
  list(params: ListParams): Promise<PageResponse<Iha>> {
    const query = new URLSearchParams({
      page: String(params.page),
      size: String(params.size),
      sort: params.sort,
    });
    if (params.durum) query.set('durum', params.durum);
    if (params.kategori) query.set('kategori', params.kategori);
    if (params.model.trim()) query.set('model', params.model.trim());
    return request(`${RESOURCE}?${query.toString()}`);
  },

  getById(id: number): Promise<Iha> {
    return request(`${RESOURCE}/${id}`);
  },

  create(body: IhaRequest): Promise<Iha> {
    return request(RESOURCE, { method: 'POST', body: JSON.stringify(body) });
  },

  update(id: number, body: IhaRequest): Promise<Iha> {
    return request(`${RESOURCE}/${id}`, { method: 'PUT', body: JSON.stringify(body) });
  },

  updateDurum(id: number, durum: IhaDurum): Promise<Iha> {
    return request(`${RESOURCE}/${id}/durum`, { method: 'PATCH', body: JSON.stringify({ durum }) });
  },

  remove(id: number): Promise<void> {
    return request(`${RESOURCE}/${id}`, { method: 'DELETE' });
  },
};
