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
	public void start() throws Exception {
		//Exception ex;
		while (true) {
			try {
				data = tryThis();
				times = maxTimes;
				return;
			}
			catch (Exception e) {
				times++;
				if (times>=maxTimes) {
					throw e;
				}
			}
		}
		//throw ex;
	}
	public E getData() {
		return data;
	}
}
