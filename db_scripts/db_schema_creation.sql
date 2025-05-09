-- Run the following SQL command to create a separate database:
CREATE DATABASE sapphire_gatewaydb;
-- use postgres root username and password and connect to the gateway db and run the following commands

-- Create a new user specifically for this database:
CREATE USER sapphire_gateway_admin WITH PASSWORD 'password';

-- Grant the user permissions to the database:
GRANT ALL PRIVILEGES ON DATABASE sapphire_gatewaydb TO sapphire_gateway_admin;

-- Create a schema for organizing tables:
GRANT ALL PRIVILEGES ON DATABASE sapphire_gatewaydb TO sapphire_gateway_admin;

-- Set the default schema for the user:
CREATE SCHEMA sapphire_gateway AUTHORIZATION sapphire_gateway_admin;

-- Now, create the table inside the gateway schema:
CREATE TABLE sapphire_gateway.routes (
                                id SERIAL PRIMARY KEY,
                                route_id VARCHAR(255) UNIQUE NOT NULL,
                                uri VARCHAR(255) NOT NULL,
                                predicates TEXT NOT NULL,
                                filters TEXT,
                                enabled BOOLEAN DEFAULT TRUE
);


-- Insert Sample Data

INSERT INTO sapphire_gateway.routes (route_id, uri, predicates, filters, enabled) VALUES
                                                                             ('member-management', 'lb:http://MEMBER-MANAGEMENT', '/api/v1/sapphire/mms/secured/member/**, /api/v1/sapphire/mms/secured/account/**', 'AuthenticationFilter', TRUE),
                                                                             ('member-management-public', 'lb:http://MEMBER-MANAGEMENT', '/api/v1/sapphire/mms/public/**', NULL, TRUE),
                                                                             ('premium-billing', 'lb:http://PREMIUM-BILLING', '/api/v1/sapphire/premium-billing/**', NULL, TRUE),
                                                                             ('auth-service', 'lb:http://AUTH-SERVICE', '/api/v1/sapphire/auth/**', NULL, TRUE),
                                                                             ('provider-management', 'lb:http://PROVIDER-MANAGEMENT', '/api/v1/sapphire/provider/private/**', 'AuthenticationFilter', TRUE),
                                                                             ('provider-management-public', 'lb:http://PROVIDER-MANAGEMENT', '/api/v1/sapphire/provider/public/**', NULL, TRUE),








