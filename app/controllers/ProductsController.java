package controllers;

import com.google.common.io.Files;
import models.Product;

import models.Tag;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import play.mvc.With;
import views.html.products.list;
import views.html.products.details;

import javax.inject.Inject;

@With(CatchAction.class)
public class ProductsController extends Controller {

    private final Form<Product> productForm;

    @Inject
    public ProductsController(FormFactory formFactory) {
        this.productForm = formFactory.form(Product.class);
    }

    public Result index() {
        return redirect(routes.ProductsController.list(0));
    }

    public Result list(Integer page) {
        List<Product> products = Product.findAll();
        return ok(list.render(products));
    }

    public Result newProduct() {
        return ok(details.render(productForm));
    }

    public Result details(Product product) {
        Form<Product> filledForm = productForm.fill(product);
        return ok(details.render(filledForm));
    }

    public Result save() {
        Form<Product> boundForm = productForm.bindFromRequest();
        if(boundForm.hasErrors()) {
            flash("error", "Please correct the form below.");
            return badRequest(details.render(boundForm));
        }
        Product product = boundForm.get();
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> part = body.getFile("picture");

        if(part != null) {
            File picture = part.getFile();
            try {
                product.picture = Files.toByteArray(picture);
            } catch (IOException e) {
                return internalServerError("Error reading file upload");
            }
        }

        List<Tag> tags = new ArrayList<Tag>();
        for (Tag tag : product.tags) {
            if (tag.id != null) {
                tags.add(Tag.findByID(tag.id));
            }
        }
        product.tags = tags;

        product.save();
        flash("success",
                String.format("Successfully added product %s", product));

        return redirect(routes.ProductsController.list(1));
    }

    public Result delete(String ean) {
        final Product product = Product.findByEan(ean);
        if(product == null) {
            return notFound(String.format("Product %s does not exists.", ean));
        }
        Product.remove(product);
        return redirect(routes.ProductsController.list(1));
    }

    public Result picture(String ean) {
        final Product product = Product.findByEan(ean);
        if(product == null) return notFound();
        return ok(product.picture);
    }

}
