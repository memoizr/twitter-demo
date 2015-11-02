package com.memoizrlabs.jeeter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.common.BasePresenter;
import com.memoizrlabs.jeeter.common.PresenterView;

/**
 * Base class for presenter tests. Extend this class whenever it is desired to
 * create a Presenter class.
 *
 * @param <P> The Presenter being tested
 * @param <V> The view associated to the Presenter View
 */
public abstract class BasePresenterTest<P extends BasePresenter<V>, V extends PresenterView> extends
        BaseMockitoTest {

    protected P presenter;
    protected V view;

    @CallSuper
    @Override
    public void setup() {
        super.setup();

        presenter = createPresenter();
        view = createView();

        setupMocks();
    }

    @NonNull
    protected abstract P createPresenter();

    @NonNull
    protected abstract V createView();

    protected void setupMocks() {
    }

    protected final void onViewAttached() {
        presenter.onViewAttached(view);
    }

    protected final void onViewDetached() {
        presenter.onViewDetached();
    }
}
