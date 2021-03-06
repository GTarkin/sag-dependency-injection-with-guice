package softwareag;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintStream;
import java.time.LocalTime;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;

public class TestablePorterTest {

  @Bind
  MorningChecker morningChecker = new MorningCheckerImpl();

  @Mock
  @Bind
  IdGenerator idGenerator;

  @Mock
  @Bind
  @Named("currentLocalTime")
  TimeSource clock;

  @Mock
  @Bind
  @Named("stdout")
  PrintStream out;

  TestablePorter porter;


  @Before
  public void setup() {
    // Init mocks generated by Mockito
    MockitoAnnotations.initMocks(this);

    when(clock.currentTime()).thenReturn(LocalTime.of(0, 0));
    when(idGenerator.generateUniqueId()).thenReturn("someId");

    // Create an injector using the BoundFieldModule, which binds any members of this class annotated with the "bind" annotation honoring other DI annotations
    Injector injector = Guice.createInjector(BoundFieldModule.of(this));
    porter = injector.getInstance(TestablePorter.class);
  }


  @Test
  public void existsWithSuccessWhenThereArePeopleToGreet() {

    assertThat(porter.greet("person1"), equalTo(TestablePorter.SUCCESS));
  }


  @Test
  public void existsWithFailureWhenThereNoPeopleToGreet() {
    String[] NOONE = {};

    int exitCode = porter.greet(NOONE);

    assertThat(exitCode, equalTo(TestablePorter.FAILURE));
    verify(out).println("someId There is noone to greet");
  }


  @Test
  public void greetsWithGoodMorningInTheMorning() {
    int hour = 8;
    int minute = 0;
    when(clock.currentTime()).thenReturn(LocalTime.of(hour, minute));

    int exitCode = porter.greet("person1", "person2");

    assertThat(exitCode, equalTo(TestablePorter.SUCCESS));
    verify(out).println("someId Good morning person1 and person2");
  }


  @Test
  public void greetsWithHelloInTheRestOfTheDay() {
    int hour = 14;
    int minute = 0;
    when(clock.currentTime()).thenReturn(LocalTime.of(hour, minute));

    int exitCode = porter.greet("person1", "person2");

    assertThat(exitCode, equalTo(TestablePorter.SUCCESS));
    verify(out).println("someId Hello person1 and person2");
  }
}
