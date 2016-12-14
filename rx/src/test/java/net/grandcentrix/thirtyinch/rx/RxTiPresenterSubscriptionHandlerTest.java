package net.grandcentrix.thirtyinch.rx;

import net.grandcentrix.thirtyinch.TiView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import rx.observers.TestSubscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class RxTiPresenterSubscriptionHandlerTest {

    private TiMockPresenter mPresenter;

    private RxTiPresenterSubscriptionHandler mSubscriptionHandler;

    private TiView mView;

    @Before
    public void setUp() throws Exception {
        mView = mock(TiView.class);
        mPresenter = new TiMockPresenter();
        mSubscriptionHandler = new RxTiPresenterSubscriptionHandler(mPresenter);
    }

    @After
    public void tearDown() throws Exception {
        mPresenter = null;
        mView = null;
        mSubscriptionHandler = null;
    }

    @Test(expected = AssertionError.class)
    public void tesManageViewSubscription_DetachBeforeAttach_ShouldThrowAssertError()
            throws Exception {
        mPresenter.create();
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

        mSubscriptionHandler.manageViewSubscription(testSubscriber);
        mPresenter.detachView();

        testSubscriber.assertUnsubscribed();
    }

    @Test(expected = IllegalStateException.class)
    public void testManageSubscription_AfterDestroy_ShouldThrowIllegalState() throws Exception {
        mPresenter.create();
        mPresenter.destroy();
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

        mSubscriptionHandler.manageSubscription(testSubscriber);
    }

    @Test
    public void testManageSubscription_WithAlreadyUnsubscribedSubscription_ShouldDoNothing()
            throws Exception {
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        testSubscriber.unsubscribe();

        mSubscriptionHandler.manageSubscription(testSubscriber);

        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManageSubscription_WithDestroy_ShouldUnsubscribe() throws Exception {
        mPresenter.create();
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

        mSubscriptionHandler.manageSubscription(testSubscriber);
        assertThat(testSubscriber.isUnsubscribed(), equalTo(false));

        mPresenter.destroy();
        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManageViewSubscription_WithDetachSingleSub_ShouldUnsubscribe()
            throws Exception {
        mPresenter.create();
        mPresenter.attachView(mView);
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

        mSubscriptionHandler.manageViewSubscription(testSubscriber);
        assertThat(testSubscriber.isUnsubscribed(), equalTo(false));

        mPresenter.detachView();
        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManageViewSubscription_WithDetachView_ShouldUnsubscribe() throws Exception {
        mPresenter.create();
        mPresenter.attachView(mView);
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

        mSubscriptionHandler.manageViewSubscription(testSubscriber);
        mPresenter.detachView();

        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManageViewSubscriptions_WithOneAlreadyUnsubscribed_ShouldNotAddToSubscription()
            throws Exception {
        mPresenter.create();
        mPresenter.attachView(mView);
        TestSubscriber<Void> firstSubscriber = new TestSubscriber<>();
        TestSubscriber<Void> secondSubscriber = new TestSubscriber<>();
        secondSubscriber.unsubscribe();

        mSubscriptionHandler.manageViewSubscription(firstSubscriber, secondSubscriber);

        assertThat(firstSubscriber.isUnsubscribed(), equalTo(false));
        secondSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManagerViewSubscriptions_WithDetach_ShouldUnsubcribe() throws Exception {
        mPresenter.create();
        mPresenter.attachView(mView);
        TestSubscriber<Void> firstSubscriber = new TestSubscriber<>();
        TestSubscriber<Void> secondSubscriber = new TestSubscriber<>();
        TestSubscriber<Void> thirdSubscriber = new TestSubscriber<>();

        mSubscriptionHandler
                .manageViewSubscription(firstSubscriber, secondSubscriber, thirdSubscriber);
        assertThat(firstSubscriber.isUnsubscribed(), equalTo(false));
        assertThat(secondSubscriber.isUnsubscribed(), equalTo(false));
        assertThat(thirdSubscriber.isUnsubscribed(), equalTo(false));

        mPresenter.detachView();
        firstSubscriber.assertUnsubscribed();
        secondSubscriber.assertUnsubscribed();
        thirdSubscriber.assertUnsubscribed();
    }
}