
import charger.ModulePlugin;
import com.michaelgrenon.chargerks.EditFrameWatcher;
import com.michaelgrenon.chargerks.KnowledgeSpace;
import javax.swing.KeyStroke;

public class KsModulePlugin implements ModulePlugin {

    private static final String NAME = "Knowledge Space";
    private static final String INFO = "Knowledge Space";
    private static final KnowledgeSpace KS = new KnowledgeSpace("bolt://localhost:7687", "neo4j", "random");
    private static EditFrameWatcher editorWatcher = new EditFrameWatcher(NAME, KS);
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke('k');
    }

    @Override
    public String getInfo() {
        return INFO;
    }

    @Override
    public void startup() {
        KS.open();
        editorWatcher.start();
    }

    @Override
    public void shutdown() {
        editorWatcher.stop();
        KS.close();
    }
    
}
