from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import PatientViewSet, MedicViewSet, AppointmentViewSet

# El Router de DRF crea las URLs para GET, POST, PUT y DELETE
router = DefaultRouter()
router.register(r'patients', PatientViewSet)
router.register(r'medics', MedicViewSet)
router.register(r'appointments', AppointmentViewSet)

urlpatterns = [
    path('', include(router.urls)),
]