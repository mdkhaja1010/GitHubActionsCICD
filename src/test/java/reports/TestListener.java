package reports;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class TestListener implements ITestListener {
	
	 ExtentReports extent = ExtentReportManager.getReporterObject();
	    ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

	    @Override
	    public void onTestStart(ITestResult result) {
	        ExtentTest test = extent.createTest(result.getMethod().getMethodName());
	        extentTest.set(test);
	    }

	    @Override
	    public void onTestSuccess(ITestResult result) {
	        extentTest.get().pass("Test Passed");
	    }

	    @Override
	    public void onTestFailure(ITestResult result) {
	        extentTest.get().fail(result.getThrowable());
	    }

	    @Override
	    public void onTestSkipped(ITestResult result) {
	        extentTest.get().skip("Test Skipped: " + result.getThrowable());
	    }

	    @Override
	    public void onFinish(ITestContext context) {
	        extent.flush();
	    }

}
