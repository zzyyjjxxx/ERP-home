import { create } from 'zustand';

export interface UserInfo {
  id: string;
  username: string;
  realName: string;
  avatar?: string;
  permissions: string[];
}

interface UserStore {
  token: string;
  user: UserInfo | null;
  setToken: (token: string) => void;
  setUser: (user: UserInfo) => void;
  logout: () => void;
  hasPermission: (perm: string) => boolean;
}

export const useUserStore = create<UserStore>((set, get) => ({
  token: localStorage.getItem('token') || '',
  user: null,
  setToken: (token) => {
    localStorage.setItem('token', token);
    set({ token });
  },
  setUser: (user) => set({ user }),
  logout: () => {
    localStorage.clear();
    set({ token: '', user: null });
  },
  hasPermission: (perm) => {
    const { user } = get();
    return user?.permissions?.includes(perm) ?? false;
  },
}));
