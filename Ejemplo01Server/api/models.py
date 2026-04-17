from django.db import models

class Patient(models.Model):
    name = models.CharField(max_length=100)
    lastName = models.CharField(max_length=100)
    email = models.EmailField()
    dni = models.IntegerField(unique=True)
    phone = models.CharField(max_length=20) 
    active = models.BooleanField(default=True)

    def __str__(self):
        return f"{self.name} {self.lastName}"

class Medic(models.Model):
    name = models.CharField(max_length=100)
    lastName = models.CharField(max_length=100)
    registration = models.CharField(max_length=50, unique=True) # Matrícula
    speciality = models.CharField(max_length=100)
    active = models.BooleanField(default=True)

    def __str__(self):
        return f"Dr. {self.lastName} ({self.speciality})"

class Appointment(models.Model):
    idPatient = models.BigIntegerField()
    idMedic = models.BigIntegerField()
    date = models.CharField(max_length=10) # Formato "YYYY-MM-DD"
    time = models.CharField(max_length=5)  # Formato "HH:MM"
    state = models.CharField(max_length=20, default="PENDIENTE")
    active = models.BooleanField(default=True)

    def __str__(self):
        return f"Turno {self.date} {self.time}"