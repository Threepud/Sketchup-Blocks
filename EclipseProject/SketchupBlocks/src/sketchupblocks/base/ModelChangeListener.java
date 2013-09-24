package sketchupblocks.base;

import sketchupblocks.construction.ModelBlock;

public interface ModelChangeListener
{
	void fireModelChangeEvent(ModelBlock change);
}