package com.khaliuk.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.khaliuk.dao.UserDao;
import com.khaliuk.dao.UserDaoImpl;
import com.khaliuk.model.User;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private UserService userService;
    private User testUser;
    private User registerUser;

    @Before
    public void setup() {
        UserDao userDao = mock(UserDaoImpl.class);
        userService = new UserServiceImpl(userDao);
        testUser = new User(1L, "lucky7",
                "96cae35ce8a9b0244178bf28e4966c2ce1b8385723a96a6b838858cdd6ca0a1e",
                "token", "Anton", "Ivanov");
        registerUser = new User("lucky7","123123",
                "Anton", "Ivanov");
        when(userDao.getByUsername("lucky7")).thenReturn(Optional.ofNullable(testUser));
        when(userDao.save(registerUser)).thenReturn(testUser);
        when(userDao.getByToken("token")).thenReturn(testUser);
    }

    @Test
    public void testAuthorizeReturnsUserWithRightPassword() {
        User inputUser = new User("lucky7", "123123");
        Optional<User> actualResult = userService.authorize(inputUser);
        assertTrue(actualResult.isPresent());
    }

    @Test
    public void testAuthorizeReturnsNullOptionalIfPasswordIsWrong() {
        User inputUser = new User("lucky7", "123123123");
        Optional<User> actualResult = userService.authorize(inputUser);
        assertFalse(actualResult.isPresent());
    }

    @Test
    public void testAddUserReturnsUserWithId() {
        Optional<User> actualResult = userService.addUser(registerUser);
        assertEquals(Long.valueOf(1L), actualResult.get().getId());
    }

    @Test
    public void testFindByTokenReturnsUserWithToken() {
        Optional<User> actualResult = userService.findByToken("token");
        assertEquals(testUser.getToken(), actualResult.get().getToken());
    }
}
