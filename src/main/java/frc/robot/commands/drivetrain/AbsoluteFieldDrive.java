package frc.robot.commands.drivetrain;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.kinematics.ChassisSpeeds; // Class to handle chassis speed calculations.
import edu.wpi.first.wpilibj.DriverStation; // Driver station for accessing driver settings like alliance.
import edu.wpi.first.wpilibj2.command.Command; // Base class for commands.
import frc.robot.Constants; // Constants including drivetrain specifications.
import frc.robot.subsystems.drivetrain.swerve.SwerveDrive; // Swerve drive subsystem for robot movement.

public class AbsoluteFieldDrive extends Command {

    private final SwerveDrive swerve;             // Reference to the swerve drive subsystem.
    private final DoubleSupplier vX, vY, heading; // Supplier functions for velocity inputs and heading.
    private int invert = 1;                        // Variable to invert direction based on alliance color.

    // Constructor to initialize the AbsoluteFieldDrive command.
    public AbsoluteFieldDrive(SwerveDrive swerve, DoubleSupplier vX, DoubleSupplier vY, DoubleSupplier heading) {
        this.swerve = swerve;
        this.vX = vX;
        this.vY = vY;
        this.heading = heading;

        addRequirements(swerve); // Specify that this command requires the swerve subsystem.
    }

    // Called when the command is initialized.
    @Override
    public void initialize() {
        boolean isRed = false;
        var alliance = DriverStation.getAlliance(); // Get the current alliance.

        // Check if the alliance is Red and set invert accordingly.
        if (alliance.isPresent()) {
            isRed = alliance.get() == DriverStation.Alliance.Red;
        }

        // Invert drive direction if the robot is on the Red alliance.
        if (isRed) {
            invert = -1;
        }
    }

    // Called repeatedly while the command is scheduled.
    @Override
    public void execute() {
        // Calculate speeds based on input and max speed constants.
        ChassisSpeeds speeds = new ChassisSpeeds(
            vX.getAsDouble() * Constants.DrivetrainConstants.kMAX_SPEED_METERS_PER_SECOND * invert,
            vY.getAsDouble() * Constants.DrivetrainConstants.kMAX_SPEED_METERS_PER_SECOND * invert,
            heading.getAsDouble() * Constants.DrivetrainConstants.kMAX_ANGULAR_SPEED_RADIANS_PER_SECOND * invert
        );

        // Drive the swerve drive subsystem with field-relative speeds.
        swerve.driveFieldRelative(speeds);
    }

    // Called when the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        // Cleanup or reset logic can be added here if necessary.
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false; // This command runs indefinitely until interrupted.
    }
}
