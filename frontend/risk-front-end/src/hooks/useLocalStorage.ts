import localForage from "localforage";
import { useEffect, useState } from "react";

export default function useLocalStorage<T>(
    key: string,
    defaultValue: T | null = null
): [T | null, (v: T) => void, () => void] {
    const [value, setValue] = useState<T | null>(defaultValue);

    useEffect(() => {
        const getValue = async () => {
            const storageValue = await localForage.getItem<T>(key);
            setValue(storageValue);
        };
        getValue();
    }, [key]);

    const setter = (newValue: T) => {
        if (newValue !== value) {
            localForage.setItem(key, newValue);
            setValue(newValue);
        }
    };

    const remove = () => {
        localForage.removeItem(key);
        setValue(null);
    };

    return [value, setter, remove];
}
