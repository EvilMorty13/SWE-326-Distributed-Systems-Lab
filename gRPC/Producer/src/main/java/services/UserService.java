package services;

import io.grpc.stub.StreamObserver;
import org.example.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class UserService extends userGrpc.userImplBase {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/userinfo";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private Connection connection;

    public UserService() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Successfully connected to the database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void signIn(User.SignInRequest request, StreamObserver<User.SignInResponse> responseObserver) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();

            PreparedStatement checkUserStmt = connection.prepareStatement("SELECT password FROM users WHERE username = ?");
            checkUserStmt.setString(1, username);
            ResultSet resultSet = checkUserStmt.executeQuery();

            if (resultSet.next()) {
                String storedPasswordHash = resultSet.getString("password");

                if (BCrypt.checkpw(password, storedPasswordHash)) {
                    User.SignInResponse response = User.SignInResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Sign-in successful")
                            .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                } else {
                    User.SignInResponse response = User.SignInResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage("Incorrect password")
                            .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
            } else {
                User.SignInResponse response = User.SignInResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("User not found")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void signUp(User.SignUpRequest request, StreamObserver<User.SignUpResponse> responseObserver) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();

            PreparedStatement checkUserStmt = connection.prepareStatement("SELECT username FROM users WHERE username = ?");
            checkUserStmt.setString(1, username);
            ResultSet resultSet = checkUserStmt.executeQuery();

            if (resultSet.next()) {
                User.SignUpResponse response = User.SignUpResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Username already exists")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                PreparedStatement insertUserStmt = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
                insertUserStmt.setString(1, username);
                insertUserStmt.setString(2, hashedPassword);

                int rowsAffected = insertUserStmt.executeUpdate();

                if (rowsAffected > 0) {
                    User.SignUpResponse response = User.SignUpResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Sign-up successful")
                            .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                } else {
                    User.SignUpResponse response = User.SignUpResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage("Sign-up failed")
                            .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createProfile(User.CreateProfileRequest request, StreamObserver<User.CreateProfileResponse> responseObserver) {
        String username = request.getUsername();
        String name = request.getName();
        int age = request.getAge();
        String occupation = request.getOccupation();

        boolean usernameExistsInUsers = false;
        try {
            PreparedStatement checkUserStmt = connection.prepareStatement("SELECT username FROM users WHERE username = ?");
            checkUserStmt.setString(1, username);
            ResultSet resultSet = checkUserStmt.executeQuery();
            usernameExistsInUsers = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!usernameExistsInUsers) {
            User.CreateProfileResponse response = User.CreateProfileResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("User does not exist in the users table")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        boolean usernameExistsInProfile = false;
        try {
            PreparedStatement checkProfileStmt = connection.prepareStatement("SELECT username FROM profile WHERE username = ?");
            checkProfileStmt.setString(1, username);
            ResultSet resultSet = checkProfileStmt.executeQuery();
            usernameExistsInProfile = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (usernameExistsInProfile) {
            User.CreateProfileResponse response = User.CreateProfileResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Profile already exists for this user")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            try {
                PreparedStatement insertProfileStmt = connection.prepareStatement("INSERT INTO profile (username, name, age, occupation) VALUES (?, ?, ?, ?)");
                insertProfileStmt.setString(1, username);
                insertProfileStmt.setString(2, name);
                insertProfileStmt.setInt(3, age);
                insertProfileStmt.setString(4, occupation);

                int rowsAffected = insertProfileStmt.executeUpdate();

                if (rowsAffected > 0) {
                    User.CreateProfileResponse response = User.CreateProfileResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Profile created successfully")
                            .build();

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                } else {
                    User.CreateProfileResponse response = User.CreateProfileResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage("Profile creation failed")
                            .build();

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateProfile(User.UpdateProfileRequest request, StreamObserver<User.UpdateProfileResponse> responseObserver) {
        String username = request.getUsername();
        String name = request.getName();
        int age = request.getAge();
        String occupation = request.getOccupation();

        boolean usernameExistsInUsers = false;
        try {
            PreparedStatement checkUserStmt = connection.prepareStatement("SELECT username FROM users WHERE username = ?");
            checkUserStmt.setString(1, username);
            ResultSet resultSet = checkUserStmt.executeQuery();
            usernameExistsInUsers = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!usernameExistsInUsers) {
            User.UpdateProfileResponse response = User.UpdateProfileResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("User does not exist in the users table")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        boolean usernameExistsInProfile = false;
        try {
            PreparedStatement checkProfileStmt = connection.prepareStatement("SELECT username FROM profile WHERE username = ?");
            checkProfileStmt.setString(1, username);
            ResultSet resultSet = checkProfileStmt.executeQuery();
            usernameExistsInProfile = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!usernameExistsInProfile) {
            User.UpdateProfileResponse response = User.UpdateProfileResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Profile does not exist for this user")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        try {
            PreparedStatement updateProfileStmt = connection.prepareStatement("UPDATE profile SET name = ?, age = ?, occupation = ? WHERE username = ?");
            updateProfileStmt.setString(1, name);
            updateProfileStmt.setInt(2, age);
            updateProfileStmt.setString(3, occupation);
            updateProfileStmt.setString(4, username);

            int rowsAffected = updateProfileStmt.executeUpdate();

            if (rowsAffected > 0) {
                User.UpdateProfileResponse response = User.UpdateProfileResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Profile updated successfully")
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                User.UpdateProfileResponse response = User.UpdateProfileResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Profile update failed")
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void seeProfile(User.SeeProfileRequest request, StreamObserver<User.SeeProfileResponse> responseObserver) {
        String username = request.getUsername();

        boolean usernameExistsInUsers = false;
        try {
            PreparedStatement checkUserStmt = connection.prepareStatement("SELECT username FROM users WHERE username = ?");
            checkUserStmt.setString(1, username);
            ResultSet userResultSet = checkUserStmt.executeQuery();
            usernameExistsInUsers = userResultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (usernameExistsInUsers) {
            boolean usernameExistsInProfile = false;
            String name = "";
            int age = 0;
            String occupation = "";

            try {
                PreparedStatement checkProfileStmt = connection.prepareStatement("SELECT name, age, occupation FROM profile WHERE username = ?");
                checkProfileStmt.setString(1, username);
                ResultSet profileResultSet = checkProfileStmt.executeQuery();

                if (profileResultSet.next()) {
                    usernameExistsInProfile = true;
                    name = profileResultSet.getString("name");
                    age = profileResultSet.getInt("age");
                    occupation = profileResultSet.getString("occupation");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (usernameExistsInProfile) {
                User.SeeProfileResponse response = User.SeeProfileResponse.newBuilder()
                        .setSuccess(true)
                        .setName(name)
                        .setAge(age)
                        .setOccupation(occupation)
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                User.SeeProfileResponse response = User.SeeProfileResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Profile not found for this user")
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        } else {
            User.SeeProfileResponse response = User.SeeProfileResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("User not found")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
