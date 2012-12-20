package sample;


public class NewTest {

	NewTest(Test1 t)
	{
		t.i = 2;
	}
	
	public static void main(String[] args)
	{
		Test1 tst = new Test1();
		tst.print();
		new NewTest(tst);
		tst.print();
	}
}
