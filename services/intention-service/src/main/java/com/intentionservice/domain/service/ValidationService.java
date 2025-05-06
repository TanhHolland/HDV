package com.intentionservice.domain.service;

import com.intentionservice.domain.exeption.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    public void validateIntentionRequest(int userId, Double startLongitude, Double startLatitude,
                                       Double destLongitude, Double destLatitude) throws ValidationException
    {
        if (userId <= 0) {
            throw new ValidationException("Id người dùng không đúng");
        }
        validateCoordinate(startLongitude, "Starting longitude");
        validateCoordinate(startLatitude, "Starting latitude");
        validateCoordinate(destLongitude, "Destination longitude");
        validateCoordinate(destLatitude, "Destination latitude");

        if (startLongitude.equals(destLongitude) && startLatitude.equals(destLatitude)) {
            throw new ValidationException("Vị trí bắt đầu và kết thúc không thể trùng nhau");
        }
    }

    private void validateCoordinate(Double coordinate, String fieldName) throws ValidationException
    {
        if (coordinate == null)
        {
            throw new ValidationException(fieldName + " không được trống");
        }

        if (fieldName.contains("longitude") && (coordinate < -180 || coordinate > 180))
        {
            throw new ValidationException("Invalid " + fieldName + ": must be between -180 and 180");
        }

        if (fieldName.contains("latitude") && (coordinate < -90 || coordinate > 90))
        {
            throw new ValidationException("Invalid " + fieldName + ": must be between -90 and 90");
        }
    }

    public void validateConfirmationRequest(int driverId, int intentionId) throws ValidationException
    {
        if (driverId <= 0)
        {
            throw new ValidationException("ID không hợp lệ");
        }
        
        if (intentionId <= 0)
        {
            throw new ValidationException("ID không hợp lệ");
        }
    }
}