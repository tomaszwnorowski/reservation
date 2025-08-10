-- Insert cars with different types (SUV, SEDAN, VAN)
INSERT INTO car (type, license_plate) VALUES
                                          ('SUV', 'ABC123'),
                                          ('SUV', 'XYZ789'),
                                          ('SEDAN', 'DEF456'),
                                          ('SEDAN', 'GHI789'),
                                          ('VAN', 'JKL012'),
                                          ('VAN', 'MNO345');

-- Insert reservations for the cars
INSERT INTO reservation (car_id, "from", "to") VALUES
                                                   ((SELECT id FROM car WHERE license_plate = 'ABC123'), '2025-08-15 09:00:00', '2025-08-15 17:00:00'),
                                                   ((SELECT id FROM car WHERE license_plate = 'ABC123'), '2025-08-16 10:00:00', '2025-08-16 14:00:00'),
                                                   ((SELECT id FROM car WHERE license_plate = 'XYZ789'), '2025-08-17 08:00:00', '2025-08-17 12:00:00'),
                                                   ((SELECT id FROM car WHERE license_plate = 'DEF456'), '2025-08-15 13:00:00', '2025-08-15 18:00:00'),
                                                   ((SELECT id FROM car WHERE license_plate = 'GHI789'), '2025-08-16 09:00:00', '2025-08-16 15:00:00'),
                                                   ((SELECT id FROM car WHERE license_plate = 'JKL012'), '2025-08-18 07:00:00', '2025-08-18 19:00:00'),
                                                   ((SELECT id FROM car WHERE license_plate = 'MNO345'), '2025-08-19 11:00:00', '2025-08-19 16:00:00');