package com.tweetapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.tweetapp.dao.ConnectDB;
import com.tweetapp.dao.TweetDao;
import com.tweetapp.dao.UserDao;
import com.tweetapp.domain.CustomError;
import com.tweetapp.domain.Gender;
import com.tweetapp.domain.User;
import com.tweetapp.domain.UserResponse;

public class UserService {

	private final String EMAILREGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

	private UserDao userRepo;
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public UserService(UserDao userRepo) {
		super();
		this.userRepo = userRepo;
	}

	public void register() {
		int choice = 0;
		String email, password, reEnterPassword, firstName, lastName, dob;
		int genderChoice;
		Gender gender = Gender.UNDEFINED;
		try {
			do {
				choice = 0;
				gender = Gender.UNDEFINED;

				System.out.println("\n//***** User Registeration (*Required fields) *****//");

				UserResponse userResponse = new UserResponse();

				System.out.print("Enter your email*: ");
				email = reader.readLine();
				if (isNullOrEmpty(email) || !isEmailValid(email))
					errorBuilder(userResponse, 422, "Invalid email.");

				System.out.print("Enter your password*: ");
				password = reader.readLine();
				System.out.print("Re-enter your password*: ");
				reEnterPassword = reader.readLine();
				if (isNullOrEmpty(password) || isNullOrEmpty(reEnterPassword) || !password.equals(reEnterPassword))
					errorBuilder(userResponse, 422, "Empty password/Password mismatch while confirmation.");

				System.out.print("Enter your first name*: ");
				firstName = reader.readLine();
				if (isNullOrEmpty(firstName))
					errorBuilder(userResponse, 422, "First name is required.");

				System.out.print("Enter your last name: ");
				lastName = reader.readLine();

				System.out.print("Gender(1-Male | 2-Female | 3-Others)*: ");
				genderChoice = Integer.parseInt(reader.readLine());
				if (genderChoice != 1 && genderChoice != 2 && genderChoice != 3)
					errorBuilder(userResponse, 422, "Gender choice is invalid.");
				else {
					gender = genderChoice == 1 ? Gender.MALE : Gender.UNDEFINED;
					gender = genderChoice == 2 ? Gender.FEMALE : gender;
					gender = genderChoice == 3 ? Gender.OTHERS : gender;
				}

				System.out.print("Date of birth (dd-MM-yyyy): ");
				dob = reader.readLine();
				if (!isValidDateOfBirth(dob))
					errorBuilder(userResponse, 422, "Invalid date of birth.");

				System.out.println("\nValidating... Please wait...");
				List<User> userList = userRepo.findByEmail(email);
				if (userList != null) {
					if (!userList.isEmpty()) {
						errorBuilder(userResponse, 422, "Email already exist.");
					}
				}
				System.out.println("Done.");

				if (userResponse.getErrors() != null) {
					if (!userResponse.getErrors().isEmpty()) {
						System.out
								.println("\nUser cannot be registered due to the below mentioned validation errors: ");
						userResponse.getErrors().forEach((err) -> System.out.println(err.getMessage()));

						System.out.println("\nDo you want to try again? \n1-Yes \n2-No (Go Back)");
						choice = Integer.parseInt(reader.readLine());
					}
				} else {
					User user = userResponse.getUser();
					user.setEmail(email);
					user.setPassword(password);
					user.setFirstName(firstName);
					user.setLastName(lastName);
					user.setGender(gender);
					user.setDob(dob);
					user.setActive(false);
					user.setId(new IdGenerator().getId());

					userRepo.save(user);
				}
			} while (choice == 1);

		} catch (NumberFormatException | IOException e) {
			System.out.println(
					"\nExpected a number for selection. Cannot process your request further, redirecting to the previous menu.");
		} catch (Exception e) {
			System.out.println("\nSomething unexpected happened. Please try again.");
		}
	}

	public void login() {
		int choice = 0;
		try {
			do {
				choice = 0;
				String email, password;

				System.out.println("\n//***** Login *****//");

				UserResponse userResponse = new UserResponse();

				System.out.print("Enter your email: ");
				email = reader.readLine();
				if (isNullOrEmpty(email) || !isEmailValid(email))
					errorBuilder(userResponse, 422, "Invalid email.");

				System.out.print("Enter your password: ");
				password = reader.readLine();
				if (isNullOrEmpty(password))
					errorBuilder(userResponse, 422, "Empty password.");

				if (userResponse.getErrors() != null) {
					if (!userResponse.getErrors().isEmpty()) {
						System.out.println("\nIncorrect email/password. Also email and password must have a value.");
						System.out.println("\nDo you want to try again? \n1-Yes \n2-No (Go Back)");
						choice = Integer.parseInt(reader.readLine());
					}
				} else {
					List<User> userList = userRepo.findByEmail(email);
					User userByEmail = null;
					if (userList != null && !userList.isEmpty()) {
						userByEmail = userList.stream()
								.filter(u -> u.getId() != null && u.getPassword().equals(password)).findFirst()
								.orElse(null);
						if (userByEmail == null) {
							System.out.println("\nIncorrect credentials.");
							System.out.println("\nDo you want to try again? \n1-Yes \n2-No (Go Back)");
							choice = Integer.parseInt(reader.readLine());
						} else {
							choice = 0;
							userResponse.setUser(userByEmail);
							userResponse.getUser().setActive(true);
							System.out.println("\nValidating... Please wait...");
							boolean login = userRepo.updateStatus(userResponse.getUser());

							if (login) {
								System.out.println("Done.");
								ConnectDB db = new ConnectDB();
								TweetDao tweetRepo = new TweetDao(db);
								TweetService tweetService = new TweetService(tweetRepo);
								tweetService.dashboard(userResponse);
							}
							System.out.println("Logging out...");
							userResponse.getUser().setActive(false);
							userRepo.updateStatus(userResponse.getUser());
						}
					} else {
						System.out.println("\nThere is no such user found.");
						System.out.println("\nDo you want to continue? \n1-Login \n2-Register (Go Back)");
						choice = Integer.parseInt(reader.readLine());
					}
				}
			} while (choice == 1);

		} catch (NumberFormatException |

				IOException e) {
			System.out.println(
					"\nExpected a number for selection. Cannot process your request further, redirecting to the previous menu.");
		} catch (Exception e) {
			System.out.println("\nSomething unexpected happened. Please try again.");
		}
	}

	public void forgotPassword() {
		int choice = 0;
		try {
			do {
				choice = 0;
				String email, dob;

				System.out.println("\n//***** Forgot Password *****//");

				UserResponse userResponse = new UserResponse();

				System.out.print("Enter your email: ");
				email = reader.readLine();
				if (isNullOrEmpty(email) || !isEmailValid(email))
					errorBuilder(userResponse, 422, "Invalid email.");

				System.out.print("Enter your date of birth (dd-MM-yyyy): ");
				dob = reader.readLine();
				if (!isValidDateOfBirth(dob))
					errorBuilder(userResponse, 422, "Invalid date of birth.");

				if (userResponse.getErrors() != null) {
					if (!userResponse.getErrors().isEmpty()) {
						System.out.println("\nValidation errors: ");
						userResponse.getErrors().forEach((err) -> System.out.println(err.getMessage()));

						System.out.println("\nDo you want to try again? \n1-Yes \n2-No (Go Back)");
						choice = Integer.parseInt(reader.readLine());
					}
				} else {
					List<User> userList = userRepo.findByEmail(email);
					User userByEmail = null;
					if (userList != null && !userList.isEmpty()) {
						userByEmail = userList.stream()
								.filter(u -> u.getId() != null && u.getDob().equalsIgnoreCase(dob)).findFirst()
								.orElse(null);
						if (userByEmail == null) {
							System.out.println("\nIncorrect date of birth provided.");
							System.out.println("\nDo you want to try again? \n1-Yes \n2-No (Go Back)");
							choice = Integer.parseInt(reader.readLine());
						} else {
							choice = 0;
							userResponse.setUser(userByEmail);

							System.out.println("\nYour password is: " + userResponse.getUser().getPassword());
						}
					} else {
						System.out.println("\nThere is no such user found.");
						System.out.println("\nDo you want to try again? \n1-Yes \n2-No (Go Back)");
						choice = Integer.parseInt(reader.readLine());
					}
				}
			} while (choice == 1);
		} catch (NumberFormatException |

				IOException e) {
			System.out.println(
					"\nExpected a number for selection. Cannot process your request further, redirecting to the previous menu.");
		} catch (Exception e) {
			System.out.println("\nSomething unexpected happened. Please try again.");
		}
	}

	public void resetPassword(UserResponse userResponse) {
		int choice = 0;
		try {
			do {
				choice = 0;
				String oldPassword;
				userResponse.setErrors(null);

				System.out.print("\nEnter your old password: ");
				oldPassword = reader.readLine();
				if (isNullOrEmpty(oldPassword))
					errorBuilder(userResponse, 422, "Empty password.");

				if (userResponse.getErrors() != null) {
					if (!userResponse.getErrors().isEmpty()) {
						System.out.println(
								"\nReset password cannot be performed if you did not provide your old password: ");

						System.out.println("\nDo you want to try again? \n1-Yes \n2-No (Go Back)");
						choice = Integer.parseInt(reader.readLine());
					}
				} else {
					List<User> userList = userRepo.findByEmail(userResponse.getUser().getEmail());
					User userByEmail = null;
					if (userList != null && !userList.isEmpty()) {
						userByEmail = userList.stream()
								.filter(u -> u.getId() != null && u.getPassword().equals(oldPassword)).findFirst()
								.orElse(null);
						if (userByEmail == null) {
							System.out.println("\nIncorrect credential.");
							System.out.println("\nDo you want to try again? \n1-Yes \n2-No (Go Back)");
							choice = Integer.parseInt(reader.readLine());
						} else {
							choice = 0;
							int internalChoice = 0;
							do {
								internalChoice = 0;
								String newPassword, reEnterPassword;
								userResponse.setErrors(null);

								System.out.print("\nEnter your new password: ");
								newPassword = reader.readLine();
								System.out.print("Re-enter your new password: ");
								reEnterPassword = reader.readLine();
								if (isNullOrEmpty(newPassword) || isNullOrEmpty(reEnterPassword)
										|| !newPassword.equals(reEnterPassword))
									errorBuilder(userResponse, 422,
											"Empty password/Password mismatch while confirmation.");

								if (userResponse.getErrors() != null) {
									if (!userResponse.getErrors().isEmpty()) {
										userResponse.getErrors().forEach((err) -> System.out
												.println("\nNew password error: " + err.getMessage()));

										System.out.println("\nDo you want to try again? \n1-Yes \n2-No (Go Back)");
										internalChoice = Integer.parseInt(reader.readLine());
									}
								} else {
									User user = userResponse.getUser();
									user.setPassword(newPassword);

									userRepo.update(user);
								}
							} while (internalChoice == 1);
						}
					} else {
						System.out.println("\nThere is no such user found.");
						System.out.println("\nDo you want to continue? \n1-Login \n2-Register (Go Back)");
						choice = Integer.parseInt(reader.readLine());
					}
				}
			} while (choice == 1);
		} catch (Exception e) {
			System.out.println(
					"\nExpected a number for selection. Cannot process your request further, Please try to reset the password again.");
		}

	}

//	Helper
	private void errorBuilder(UserResponse userResponse, int code, String message) {
		CustomError error = new CustomError();
		error.setCode(code);
		error.setMessage(message);
		if (userResponse.getErrors() == null || userResponse.getErrors().isEmpty()) {
			List<CustomError> errorList = new ArrayList<>();
			errorList.add(error);
			userResponse.setErrors(errorList);
		} else {
			userResponse.getErrors().add(error);
		}
	}

	private boolean isEmailValid(String email) {
		Pattern pat = Pattern.compile(EMAILREGEX);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}

	private boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
	}

	private boolean isValidDateOfBirth(String input) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		try {
			format.parse(input);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

}