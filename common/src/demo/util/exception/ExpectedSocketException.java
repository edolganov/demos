package demo.util.exception;


@SuppressWarnings("serial")
public class ExpectedSocketException extends BaseExpectedException {

	public ExpectedSocketException(Throwable socketCause, Throwable rootCause) {
		super("rootCauseClass="+rootCause.getClass().getName()+", socketCause="+socketCause, rootCause);
	}
	
	

}
