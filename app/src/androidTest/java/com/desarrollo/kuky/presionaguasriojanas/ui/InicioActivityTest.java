package com.desarrollo.kuky.presionaguasriojanas.ui;


import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Modulo;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class InicioActivityTest {

    @Before
    public void setUp() throws Exception {
        Usuario u = new Usuario();
        LoginActivity.usuario = u;
        Modulo modulo = new Modulo();
        modulo.setId(1);
        modulo.setNombre("presion");
        ArrayList<Modulo> modulos = new ArrayList<>();
        modulos.add(modulo);
        LoginActivity.usuario.setModulos(modulos);
    }

    @Rule
    public ActivityTestRule<InicioActivity> mActivityTestRule
            = new ActivityTestRule<>(InicioActivity.class);

    @Test
    public void changeText_sameActivity() {
        // Type text and then press the button.
//        onView(withId(R.id.etMail))
//                .perform(typeText("Modulo nuevo"), closeSoftKeyboard());
        onView(withId(R.id.bModuloPresion)).perform(click());

        // Check that the text was changed.
//        onView(withId(R.id.textToBeChanged)).check(matches(withText(STRING_TO_BE_TYPED)));
    }
}