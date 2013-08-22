package sketchupblocks.network;
import sketchupblocks.base.Model;
import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;

public interface Lobby
{
    public void updateModel(ModelBlock modelBlock);
    public Model getModel() throws Exception;
    public void setModel(Model model);
    public void registerChangeListener(ModelChangeListener listener);
}