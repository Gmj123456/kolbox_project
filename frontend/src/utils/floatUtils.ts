/**
 * 浮点数运算工具类
 * 使用 BigNumber.js 确保浮点数运算精度
 */
// @ts-ignore
import BigNumber from 'bignumber.js';

// 以下函数已废弃，现在使用 BigNumber.js 进行运算
// 保留用于向后兼容，但不再使用

/**
 * 检查并转换数字，如果为空、null 或 undefined 则返回 0
 */
function checkNum(num: string | number | null | undefined): number {
  if (num === '' || num === null || num === undefined) {
    return 0;
  }
  return Number(num);
}

/**
 * 浮点数加法
 * @param a 加数1
 * @param b 加数2
 * @returns 结果（保留2位小数）
 */
export function floatAdd(a: string | number | null | undefined, b: string | number | null | undefined): string {
  const numA = checkNum(a);
  const numB = checkNum(b);
  return new BigNumber(numA).plus(new BigNumber(numB)).toFixed(2, BigNumber.ROUND_HALF_UP);
}

/**
 * 浮点数减法
 * @param a 被减数
 * @param b 减数
 * @returns 结果（保留2位小数）
 */
export function floatSubtract(a: string | number | null | undefined, b: string | number | null | undefined): string {
  const numA = checkNum(a);
  const numB = checkNum(b);
  return new BigNumber(numA).minus(new BigNumber(numB)).toFixed(2, BigNumber.ROUND_HALF_UP);
}

/**
 * 浮点数乘法
 * @param a 乘数1
 * @param b 乘数2
 * @returns 结果（保留2位小数）
 */
export function floatMultiply(a: string | number | null | undefined, b: string | number | null | undefined): string {
  const numA = checkNum(a);
  const numB = checkNum(b);
  return new BigNumber(numA).times(new BigNumber(numB)).toFixed(2, BigNumber.ROUND_HALF_UP);
}

/**
 * 浮点数除法
 * @param a 被除数
 * @param b 除数
 * @returns 结果（保留2位小数）
 */
export function floatDivide(a: string | number | null | undefined, b: string | number | null | undefined): string {
  const numA = checkNum(a);
  const numB = checkNum(b);
  if (numB === 0) {
    throw new Error('除数不能为0');
  }
  return new BigNumber(numA).div(new BigNumber(numB)).toFixed(2, BigNumber.ROUND_HALF_UP);
}