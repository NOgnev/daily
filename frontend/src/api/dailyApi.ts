import { fetchWithAuth } from './fetchWithAuth';

export type DialogItemType = 'question' | 'answer' | 'final';

export interface DialogItem {
  id: string;
  content: string;
  type: DialogItemType;
}

function formatLocalDate(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // Месяцы от 0
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`; // YYYY-MM-DD
}

export const dailyApi = {
  getDialog: (date: Date) =>
    fetchWithAuth<DialogItem[]>({
      url: `/daily/dialog`,
      method: 'GET',
      params: {
        date: formatLocalDate(date),
      },
    }),
  next: (date: Date, content: string | null) =>
    fetchWithAuth<DialogItem[]>({
      url: `/daily/dialog`,
      method: 'POST',
      params: {
        date: formatLocalDate(date),
        content: content
      },
    }),
}