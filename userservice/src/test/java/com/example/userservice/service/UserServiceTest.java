package com.example.userservice.service;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@SpringBootTest
class UserServiceTest {


	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Test
	public void testCreateUser() {
		// Создаем объект пользователя
		User user = new User();
		user.setName("John Doe");
		user.setEmail("john@example.com");
		user.setPassword("password123");

		// Создаем объект пользователя, который будет возвращен методом save с установленным ID
		User savedUser = new User();
		savedUser.setId(1L); // Устанавливаем ID, чтобы избежать ошибки NullPointerException
		savedUser.setName("John Doe");
		savedUser.setEmail("john@example.com");
		savedUser.setPassword("encodedPassword123");

		// Настраиваем моки для passwordEncoder и userRepository
		when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword123");
		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		// Вызываем метод и проверяем результат
		User createdUser = userService.createUser(user);
		assertEquals("John Doe", createdUser.getName());
		assertEquals("john@example.com", createdUser.getEmail());
		assertEquals("encodedPassword123", createdUser.getPassword());
		assertNotNull(createdUser.getId());

		verify(userRepository).save(any(User.class));
	}

	@Test
	public void testGetUserById(){
		//создаем объект пользователя
		User user = new User();
		user.setId(1L);
		user.setName("John Doe");
		user.setEmail("john@example.com");

		//мокируем поведение репозитория
		when(userRepository.findById(1L))
				.thenReturn(Optional.of(user));

		//вызываем метод сервиса
		Optional<User> foundUser = userService.getUserById(1L);

		//проверяем что пользователь был найден правильно
		assertEquals("John Doe", foundUser.get().getName());
		assertEquals("john@example.com", foundUser.get().getEmail());

		//проверяем что метод findById был вызван в репозитории
		verify(userRepository).findById(1L);
	}


	@Test
	public void testGetUserById_NotFound() {
		//мокируем поведение репозитория на случай отсутствия пользователя
		when(userRepository.findById(1L))
				.thenReturn(Optional.empty());

		//вызываем метод сервиса
		Optional<User> foundUser = userService.getUserById(1L);

		//проверяем что пользователь не был найден
		assertFalse(foundUser.isPresent());

		//проверяем что метод findById был вызван в репозитории
		verify(userRepository).findById(1L);

	}

	@Test
	public void testGetAllUsers(){
		//создаем список пользователей
		List<User> users = new ArrayList<>();

		User user1 = new User();
		user1.setId(1L);
		user1.setName("John Doe");
		user1.setEmail("john@example.com");

		User user2 = new User();
		user2.setId(2L);
		user2.setName("Jane Doe");
		user2.setEmail("jane@example.com");

		users.add(user1);
		users.add(user2);

		//мокируем поведение репозитория
		when(userRepository.findAll())
				.thenReturn(users);

		//вызываем метод сервиса
	List<User> foundUsers = userService.getAllUsers();

	//Проверяем что список пользователей был найден правильно
		assertEquals(2, foundUsers.size());
		assertEquals("John Doe", foundUsers.get(0).getName());
		assertEquals("Jane Doe", foundUsers.get(1).getName());


		//проверяем что метод findAll был вызван в репозитории
		verify(userRepository).findAll();
	}

	@Test
	public void testGetUserByEmail(){

		User user = new User();
		user.setId(1L);
		user.setName("John Doe");
		user.setEmail("john@example.com");

		//мокируем поведение репозитория
		when(userRepository.findByEmail("john@example.com"))
				.thenReturn(Optional.of(user));

		//вызываем метод сервиса
		Optional<User> foundUser = userService.getUserByEmail("john@example.com");

		//Проверяем что возвращенный пользователь имеет правильное имя и email
		assertNotNull(foundUser);
		assertEquals("John Doe", foundUser.get().getName());
		assertEquals("john@example.com",foundUser.get().getEmail());

		//проверяем что метод findByEmail был вызван c правильным аргументом
		verify(userRepository).findByEmail("john@example.com");
	}


	@Test
	public void testUpdateUser(){
		// создаем mock пользователя
		User existingUser = new User(1L, "Old Name","old@example.com");
		User updatedUserDetails = new User(1L, "New Name", "new@example.com");

		//Симулируем нахождение пользователя в базе
		when(userRepository.findById(1L))
				.thenReturn(Optional.of(existingUser));

		when(userRepository.save(any(User.class)))
				.thenReturn(updatedUserDetails);

		// Обновляем пользователя
		User result = userService.updateUser(1L,updatedUserDetails);

		//Проверяем что данные обновлены
		assertNotNull(result);
		assertEquals("new@example.com", result.getEmail());
		assertEquals("New Name", result.getName());

		// Проверяем, что методы findById и save были вызваны
		verify(userRepository).findById(1L);
		verify(userRepository).save(existingUser);

	}


	@Test
	public void testDeleteUser() {
		// Создаем mock-пользователя
		User existingUser = new User(1L, "test@example.com", "Test User");

		// Симулируем нахождение пользователя в базе
		when(userRepository.findById(1L))
				.thenReturn(Optional.of(existingUser));

		// Удаляем пользователя
		userService.deleteUser(1L);

		// Проверяем, что репозиторий использован для удаления
		verify(userRepository, times(1))
				.delete(existingUser);
	}
}

