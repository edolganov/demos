package demo.util;

public class MathUtil {
	
	/**
	 * Округление числа в большую сторону.
	 * Если число отрицательное, то сначала округляем модуль, а потом возвращаем ему знак.
	 * <p>Пример:
	 * <pre> roundUp(119, 30) -> 120
	 * roundUp(120, 30) -> 120
	 * roundUp(121, 30) -> 150
	 * roundUp(-121, 30) -> -150
	 * </pre>
	 * @param val входное число
	 * @param delta число на которое округляем
	 */
	public static long roundUp(long val, long delta){
		boolean isNegative = val < 0;
		val = Math.abs(val);
		
		long old = val;
		val = val  / delta * delta;
		if(val < old) val += delta;
		
		return !isNegative? val : -val;
	}
	
	
	public static long roundDown(long val, long delta){
		boolean isNegative = val < 0;
		val = Math.abs(val);
		val = val  / delta * delta;
		return !isNegative? val : -val;
	}

}
