package sketchupblocks.base;

public interface ModelChangeListener
{
	void fireModelChangeEvent(ModelBlock change);
}