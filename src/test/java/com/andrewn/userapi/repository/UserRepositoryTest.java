package com.andrewn.userapi.repository;

import com.andrewn.userapi.model.users.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.failBecauseExceptionWasNotThrown;


@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    private static final String DEFAULT_FIRST_NAME = "Lrrr";
    private static final String DEFAULT_LAST_NAME = "RulerOfThePlanetOmecronPersei8";
    private static final String SORT_FIELD = "lastName";

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    public void testFindByFirstName() {
        populateTestData(1, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        populateTestData(2, "Andrew", DEFAULT_LAST_NAME);

        OffsetLimitRequest offsetLimitRequest = new OffsetLimitRequest(0, 10, new Sort(Sort.Direction.ASC, SORT_FIELD));
        List<User> found = userRepository.findByFirstName("Andrew", offsetLimitRequest).getContent();

        assertThat(found.size()).isEqualTo(1);
        assertThat("Andrew".equals(found.get(0).getFirstName()));
    }

    @Test
    public void testFindByLastName() {
        populateTestData(1, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        populateTestData(2, DEFAULT_FIRST_NAME, "Nash");

        OffsetLimitRequest offsetLimitRequest = new OffsetLimitRequest(0, 10, new Sort(Sort.Direction.ASC, SORT_FIELD));
        List<User> found = userRepository.findByLastName("Nash", offsetLimitRequest).getContent();

        assertThat(found.size()).isEqualTo(1);
        assertThat("Nash".equals(found.get(0).getLastName()));
    }

    @Test
    public void testFindByFirstNameAndLastName() {
        populateTestData(1, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        populateTestData(2, "Andrew", "Nash");

        OffsetLimitRequest offsetLimitRequest = new OffsetLimitRequest(0, 10, new Sort(Sort.Direction.ASC, SORT_FIELD));
        List<User> found = userRepository.findByFirstNameAndLastName("Andrew", "Nash", offsetLimitRequest).getContent();

        assertThat(found.size()).isEqualTo(1);
        assertThat("Andrew".equals(found.get(0).getFirstName()));
        assertThat("Nash".equals(found.get(0).getLastName()));
    }

    @Test
    public void testFindByLimitAndOffset_thenReturnUsers() {

        IntStream.rangeClosed(1, 25)
                .forEach(i -> populateTestData(i, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME + i));

        // Add different last names to test sort functionality
        populateTestData(77, "John", "Anyman");
        populateTestData(42, "John", "Everyman");
        populateTestData(56, "John", "Someone");

        OffsetLimitRequest offsetLimitRequest = new OffsetLimitRequest(0, 10, new Sort(Sort.Direction.ASC, SORT_FIELD));
        List<User> found = userRepository.findAll(offsetLimitRequest).getContent();

        assertThat(found.size()).isEqualTo(10);
        assertThat(found.get(0).getLastName()).isEqualTo("Anyman");
        assertThat(found.get(1).getLastName()).isEqualTo("Everyman");
        assertThat(found
                .stream()
                .anyMatch(user -> "Someone".equals(user.getLastName())))
                .isFalse();
    }

    @Test
    public void testFindByFirstName_withLimitAndOffset_thenReturnUsers() {

        IntStream.rangeClosed(1, 25)
                .forEach(i -> populateTestData(i, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME + i));

        OffsetLimitRequest offsetLimitRequest = new OffsetLimitRequest(10, 10, new Sort(Sort.Direction.ASC, SORT_FIELD));
        List<User> found = userRepository.findByFirstName(DEFAULT_FIRST_NAME, offsetLimitRequest).getContent();

        assertThat(found.size()).isEqualTo(10);
    }

    @Test
    public void testFindByFirstName_withLimitAndOffset_lastPage_thenReturnUsers() {

        IntStream.rangeClosed(1, 25)
                .forEach(i -> populateTestData(i, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME + i));

        OffsetLimitRequest offsetLimitRequest = new OffsetLimitRequest(20, 10, new Sort(Sort.Direction.ASC, SORT_FIELD));
        List<User> found = userRepository.findByFirstName(DEFAULT_FIRST_NAME, offsetLimitRequest).getContent();

        assertThat(found.size()).isEqualTo(5);
    }

    @Test
    public void testFindByLastName_withLimitAndOffset_thenReturnUsers() {

        IntStream.rangeClosed(1, 25)
                .forEach(i -> populateTestData(i, DEFAULT_FIRST_NAME + i, DEFAULT_LAST_NAME));

        OffsetLimitRequest offsetLimitRequest = new OffsetLimitRequest(10, 10, new Sort(Sort.Direction.ASC, SORT_FIELD));
        List<User> found = userRepository.findByLastName(DEFAULT_LAST_NAME, offsetLimitRequest).getContent();

        assertThat(found.size()).isEqualTo(10);
    }

    @Test
    public void testFindByLastName_withLimitAndOffset_lastPage_thenReturnUsers() {

        IntStream.rangeClosed(1, 25)
                .forEach(i -> populateTestData(i, DEFAULT_FIRST_NAME + i, DEFAULT_LAST_NAME));

        OffsetLimitRequest offsetLimitRequest = new OffsetLimitRequest(20, 10, new Sort(Sort.Direction.ASC, SORT_FIELD));
        List<User> found = userRepository.findByLastName(DEFAULT_LAST_NAME, offsetLimitRequest).getContent();

        assertThat(found.size()).isEqualTo(5);
    }

    @Test
    public void testSaveNewUser() {
        User newUser = new User(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        User savedUser = userRepository.save(newUser);
        assertThat(savedUser.getId()).isNotNull();
    }
    
    @Test
    public void testUpdateExistingUser() {
        User newUser = new User(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        User savedUser = userRepository.save(newUser);
        assertThat(savedUser.getId()).isNotNull();

        savedUser.setFirstName("First");
        savedUser.setLastName("Last");
        User updated = userRepository.save(savedUser);

        assertThat(updated.getId()).isEqualTo(savedUser.getId());
        assertThat("First").isEqualTo(updated.getFirstName());
        assertThat("Last").isEqualTo(updated.getLastName());
    }

    @Test
    public void testSaveNewUser_DuplicateName() {
        User newUser = new User(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        User savedUser = userRepository.save(newUser);
        assertThat(savedUser.getId()).isNotNull();

        User duplicateUser = new User(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        try {
            userRepository.save(duplicateUser);
        }
        catch(DataIntegrityViolationException e) {
            // passed test, exception expected
            return;
        }

        failBecauseExceptionWasNotThrown(DataIntegrityViolationException.class);
    }

    @Test
    public void testSaveExistingUser_DuplicateName() {
        User user1 = new User(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        user1 = userRepository.saveAndFlush(user1);
        assertThat(user1.getId()).isNotNull();

        User user2 = new User("Andrew", DEFAULT_LAST_NAME);
        user2 = userRepository.saveAndFlush(user2);
        assertThat(user2.getId()).isNotNull();

        User lookedUpUser = userRepository.findById(user2.getId()).get();

        lookedUpUser.setFirstName(DEFAULT_FIRST_NAME);
        try {
            lookedUpUser = userRepository.saveAndFlush(userRepository.save(lookedUpUser));
        }
        catch(DataIntegrityViolationException e) {
            // passed test, exception expected
            return;
        }

        failBecauseExceptionWasNotThrown(DataIntegrityViolationException.class);
    }

    private void populateTestData(int id, String firstName, String lastName) {
        User user = new User(id, firstName, lastName);
        entityManager.merge(user);
        entityManager.flush();
    }
}
