import { describe, expect, it } from 'vitest';
import { EMPTY_FORM, toRequest, todayIso, validate, type IhaFormValues } from './validation';

const TODAY = '2024-06-30';

const valid: IhaFormValues = {
  ...EMPTY_FORM,
  seriNo: 'TB2-0099',
  model: 'Bayraktar TB2',
  uretici: 'Baykar',
  kategori: 'MALE',
  durum: 'AKTIF',
  maksIrtifaMetre: '7620',
  menzilKm: '150',
  havadaKalmaSuresiSaat: '27',
  azamiKalkisAgirligiKg: '700',
  uretimTarihi: '2020-01-01',
  sonBakimTarihi: '2024-01-01',
};

describe('validate', () => {
  it('geçerli formda hata döndürmez', () => {
    expect(validate(valid, TODAY)).toEqual({});
  });

  it('boş formda zorunlu alanları raporlar', () => {
    const errors = validate(EMPTY_FORM, TODAY);
    expect(Object.keys(errors).sort()).toEqual([
      'azamiKalkisAgirligiKg',
      'havadaKalmaSuresiSaat',
      'maksIrtifaMetre',
      'menzilKm',
      'model',
      'seriNo',
      'uretici',
      'uretimTarihi',
    ]);
  });

  it('pozitif olmayan ve ondalıklı irtifayı reddeder', () => {
    expect(validate({ ...valid, menzilKm: '0' }, TODAY).menzilKm).toBe('Menzil pozitif olmalıdır');
    expect(validate({ ...valid, maksIrtifaMetre: '10.5' }, TODAY).maksIrtifaMetre)
      .toBe('Maksimum irtifa tam sayı olmalıdır');
  });

  it('gelecekteki tarihleri reddeder', () => {
    expect(validate({ ...valid, uretimTarihi: '2024-07-01' }, TODAY).uretimTarihi)
      .toBe('Üretim tarihi gelecekte olamaz');
    expect(validate({ ...valid, sonBakimTarihi: '2024-07-01' }, TODAY).sonBakimTarihi)
      .toBe('Son bakım tarihi gelecekte olamaz');
  });

  it('bakım tarihi üretimden önce olamaz', () => {
    expect(validate({ ...valid, sonBakimTarihi: '2019-12-31' }, TODAY).sonBakimTarihi)
      .toBe('Son bakım tarihi, üretim tarihinden önce olamaz');
  });

  it('seri no uzunluk sınırını uygular', () => {
    expect(validate({ ...valid, seriNo: 'X'.repeat(51) }, TODAY).seriNo)
      .toBe('Seri numarası en fazla 50 karakter olabilir');
  });
});

describe('toRequest', () => {
  it('sayıları dönüştürür, boş bakım tarihini null yapar ve boşlukları kırpar', () => {
    const req = toRequest({ ...valid, seriNo: '  TB2-0099 ', sonBakimTarihi: '' });
    expect(req.seriNo).toBe('TB2-0099');
    expect(req.maksIrtifaMetre).toBe(7620);
    expect(req.menzilKm).toBe(150);
    expect(req.sonBakimTarihi).toBeNull();
  });
});

describe('todayIso', () => {
  it('yerel tarihi sıfır dolgulu biçimlendirir', () => {
    expect(todayIso(new Date(2024, 0, 5))).toBe('2024-01-05');
  });
});
