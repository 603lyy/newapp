package cn.yaheen.online.interfaces;
public interface IUndoRedoCommand {

	public void undo();
    public void redo();
    public boolean canUndo();
    public boolean canRedo();
    public void onDeleteFromUndoStack();
    public void onDeleteFromRedoStack();
}
