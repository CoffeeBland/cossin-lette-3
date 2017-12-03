package com.coffeebland.cossinlette3.editor;

import com.coffeebland.cossinlette3.editor.ui.Operation;
import com.coffeebland.cossinlette3.utils.NtN;

/**
 * Created by Guillaume on 2015-08-30.
 */
public interface OperationExecutor {
    void execute(@NtN Operation operation, boolean runOp);
    void undo();
    void redo();
}
