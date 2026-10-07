import React, { useContext } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { AuthContext } from '../context/AuthContext';
import { ActivityIndicator, View } from 'react-native';

// Auth Screens
import LoginScreen from '../screens/Auth/LoginScreen';
import RegisterScreen from '../screens/Auth/RegisterScreen';

// Dashboard Screens (Placeholders for now)
import CitizenDashboard from '../screens/Dashboard/CitizenDashboard';
import VolunteerDashboard from '../screens/Dashboard/VolunteerDashboard';
import DutyOfficerDashboard from '../screens/Dashboard/DutyOfficerDashboard';
import DMCOfficerDashboard from '../screens/Dashboard/DMCOfficerDashboard';
import DistrictOfficerDashboard from '../screens/Dashboard/DistrictOfficerDashboard';
import RescueTeamDashboard from '../screens/Dashboard/RescueTeamDashboard';
import ShelterDashboard from '../screens/Dashboard/ShelterDashboard';
import ResourceProviderDashboard from '../screens/Dashboard/ResourceProviderDashboard';
import AdminDashboard from '../screens/Dashboard/AdminDashboard';

const Stack = createStackNavigator();
const Tab = createBottomTabNavigator();

const AuthStack = () => (
  <Stack.Navigator screenOptions={{ headerShown: false }}>
    <Stack.Screen name="Login" component={LoginScreen} />
    <Stack.Screen name="Register" component={RegisterScreen} />
  </Stack.Navigator>
);

const AppNavigator = () => {
  const { user, isLoading } = useContext(AuthContext);

  if (isLoading) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" color="#1a73e8" />
      </View>
    );
  }

  return (
    <NavigationContainer>
      {user ? (
        <Stack.Navigator screenOptions={{ headerShown: false }}>
          {user.role === 'CITIZEN' && <Stack.Screen name="CitizenDashboard" component={CitizenDashboard} />}
          {user.role === 'VOLUNTEER' && <Stack.Screen name="VolunteerDashboard" component={VolunteerDashboard} />}
          {user.role === 'DUTY_OFFICER' && <Stack.Screen name="DutyOfficerDashboard" component={DutyOfficerDashboard} />}
          {user.role === 'DMC_OFFICER' && <Stack.Screen name="DMCOfficerDashboard" component={DMCOfficerDashboard} />}
          {user.role === 'DISTRICT_OFFICER' && <Stack.Screen name="DistrictOfficerDashboard" component={DistrictOfficerDashboard} />}
          {user.role === 'RESCUE_TEAM' && <Stack.Screen name="RescueTeamDashboard" component={RescueTeamDashboard} />}
          {user.role === 'SHELTER_COORDINATOR' && <Stack.Screen name="ShelterDashboard" component={ShelterDashboard} />}
          {user.role === 'RESOURCE_PROVIDER' && <Stack.Screen name="ResourceProviderDashboard" component={ResourceProviderDashboard} />}
          {user.role === 'ADMIN' && <Stack.Screen name="AdminDashboard" component={AdminDashboard} />}
        </Stack.Navigator>
      ) : (
        <AuthStack />
      )}
    </NavigationContainer>
  );
};

export default AppNavigator;
