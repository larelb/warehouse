package controllers;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import utils.ExceptionMailer;

import java.util.concurrent.CompletionStage;

public class CatchAction extends Action.Simple {
    public CompletionStage<Result> call(Http.Context ctx) {
        try {
            return delegate.call(ctx);
        } catch (Throwable e) {
            ExceptionMailer.send(e);
            throw new RuntimeException(e);
        }
    }
}
