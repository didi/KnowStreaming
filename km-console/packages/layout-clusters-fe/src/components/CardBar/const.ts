export const healthScoreCondition = () => {
  const n = Date.now()
  return {
    startTime: n - 5 * 60 * 1000,
    endTime: n,
    aggType: 'avg'
  }
}
