/**
 * Format a number by rounding it to the nearest integer and adding locale-specific thousand separators
 * @param {number|string} value - The value to format
 * @returns {string} - Formatted integer string with thousand separators
 * @example
 * formatNumber(1234.56) // "1,235"
 * formatNumber(9876.4) // "9,876"
 */
export function formatNumber(value) {
  const n = Number(value) || 0;
  return Math.round(n).toLocaleString(undefined, {minimumFractionDigits: 0, maximumFractionDigits: 0});
}
