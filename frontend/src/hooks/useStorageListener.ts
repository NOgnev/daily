import { useEffect } from 'react';

const useStorageListener = (key: string, callback: () => void): void => {
    useEffect(() => {
        const handleStorage = (e: StorageEvent) => {
            if (e.key === key) {
                callback();
            }
        };

        window.addEventListener('storage', handleStorage);
        return () => window.removeEventListener('storage', handleStorage);
    }, [key, callback]);
};

export default useStorageListener;