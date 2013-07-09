package main.structures;

public abstract class MultipleTry<E> {
	int times = 0;
	int maxTimes = 3;
	public E data = null;
	public final Object[] input;
	public MultipleTry(int maxTimes, Object[] input) {
		this.maxTimes = maxTimes;
		this.input = input;
	}
	public abstract E tryThis() throws Exception;
	public void start() {
		while (times<maxTimes) {
			try {
				data = tryThis();
				times = maxTimes;
			}
			catch (Exception e) {
				times++;
			}
		}
	}
	public E getData() {
		return data;
	}
}
