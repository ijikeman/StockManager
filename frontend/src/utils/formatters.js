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

/**
 * Format a number with 2 decimal places and locale-specific thousand separators
 * Used for stock prices and dividend amounts
 * @param {number|string} value - The value to format
 * @returns {string} - Formatted number string with 2 decimal places
 * @example
 * formatDecimal(1234.567) // "1,234.57"
 * formatDecimal(9876.4) // "9,876.40"
 */
export function formatDecimal(value) {
  const n = Number(value) || 0;
  return n.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2});
}
