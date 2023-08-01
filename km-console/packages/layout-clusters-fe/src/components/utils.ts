import { useCallback, useState } from 'react';

export function useForceRefresh() {
  const [refreshKey, setRefresh] = useState<number>(0);
  const forceRefresh: () => void = useCallback(() => {
    setRefresh((x) => x + 1);
  }, []);

  return [refreshKey, forceRefresh];
}
