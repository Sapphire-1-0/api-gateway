-- Run the following SQL command to create a separate database:
CREATE DATABASE gatewaydb;
-- use postgres root username and password and connect to the gateway db and run the following commands

-- Create a new user specifically for this database:
CREATE USER gatewayadmin WITH PASSWORD 'gateway_pass';

-- Grant the user permissions to the database:
GRANT ALL PRIVILEGES ON DATABASE gatewaydb TO gatewayadmin;

-- Create a schema for organizing tables:
CREATE SCHEMA gateway AUTHORIZATION gatewayadmin;

-- Set the default schema for the user:
ALTER ROLE gatewayadmin SET search_path TO gateway;

-- Now, create the table inside the gateway schema:
CREATE TABLE gateway.routes (
                                id SERIAL PRIMARY KEY,
                                route_id VARCHAR(255) UNIQUE NOT NULL,
                                uri VARCHAR(255) NOT NULL,
                                predicates TEXT NOT NULL,
                                filters TEXT,
                                enabled BOOLEAN DEFAULT TRUE
);


-- Insert Sample Data

INSERT INTO gateway.routes (route_id, uri, predicates, filters, enabled) VALUES
                                                                             ('member-management', 'lb:http://MEMBER-MANAGEMENT', '/api/v1/sapphire/mms/secured/member/**, /api/v1/sapphire/mms/secured/account/**', 'AuthenticationFilter', TRUE),
                                                                             ('member-management-public', 'lb:http://MEMBER-MANAGEMENT', '/api/v1/sapphire/mms/public/**', NULL, TRUE),
                                                                             ('premium-billing', 'lb:http://PREMIUM-BILLING', '/api/v1/sapphire/premium-billing/**', NULL, TRUE),
                                                                             ('auth-service', 'lb:http://AUTH-SERVICE', '/api/v1/sapphire/auth/**', NULL, TRUE);








