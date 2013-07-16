class ModelBlock
{
	enum ChangeType
	{
		ADD,
		UPDATE,
		DELETE
	}
	
	public SmartBlock smartBlock;
	public float[][] world;
	public ChangeType type;
}