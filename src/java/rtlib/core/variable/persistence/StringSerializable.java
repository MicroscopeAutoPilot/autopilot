package rtlib.core.variable.persistence;

public interface StringSerializable
{

	@Override
	public String toString();

	public void fromString(String pString);
}
