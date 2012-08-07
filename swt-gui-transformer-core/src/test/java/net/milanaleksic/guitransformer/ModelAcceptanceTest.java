package net.milanaleksic.guitransformer;

import com.google.common.base.Optional;
import net.milanaleksic.guitransformer.model.TransformerModel;
import net.milanaleksic.guitransformer.test.GuiceRunner;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(GuiceRunner.class)
public class ModelAcceptanceTest {

    @Inject
    private Transformer transformer;

    @TransformerModel
    private ModelAcceptanceTestModel model;

    @Test
    public void from_form_to_model() throws TransformerException {
        final TransformationContext transformationContext = transformer.fillManagedForm(this);
        Optional<Text> text = transformationContext.getMappedObject("text1");
        final Text text1 = text.get();
        text1.setText("test value");
        assertThat(model, notNullValue());
        assertThat(model.getText1(), notNullValue());
        assertThat(model.getText1(), equalTo("test value"));
    }

    @Test
    public void from_model_to_form() throws TransformerException {
        final TransformationContext transformationContext = transformer.fillManagedForm(this);
        Optional<Text> text = transformationContext.getMappedObject("text1");
        final Text text1 = text.get();
        text1.setText("test value");
        assertThat(model, notNullValue());
        assertThat(model.getText1(), notNullValue());
        assertThat(model.getText1(), equalTo("test value"));
        model.updateForm();
    }

}
