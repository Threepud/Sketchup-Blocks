package sketchupblocks.network;
import sketchupblocks.base.Model;
import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;

public interface Lobby
{
    public void updateModel(ModelBlock modelBlock);
    public Model getModel();
    public void setModel(Model model);
    public void registerChangeListener(ModelChangeListener listener);
}