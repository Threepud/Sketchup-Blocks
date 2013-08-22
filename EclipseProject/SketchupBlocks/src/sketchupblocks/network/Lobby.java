package sketchupblocks.network;
import sketchupblocks.base.Model;
import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;
import sketchupblocks.exception.ModelNotSetException;

public interface Lobby
{
    public void updateModel(ModelBlock modelBlock);
    public Model getModel() throws ModelNotSetException;
    public void setModel(Model model);
    public void registerChangeListener(ModelChangeListener listener);
}