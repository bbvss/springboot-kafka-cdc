SELECT [name], database_id, is_cdc_enabled
FROM sys.databases
GO
SELECT [name], is_tracked_by_cdc
FROM sys.tables
GO

USE testDB
GO
EXEC sys.sp_cdc_enable_db
GO

EXEC sys.sp_cdc_enable_table
     @source_schema = N'dbo',
     @source_name = N'users',
     @role_name = NULL,
     @supports_net_changes = 0
GO

EXEC sys.sp_cdc_disable_table
     @source_schema = N'dbo',
     @source_name = N'users',
     @capture_instance = N'dbo_users'
GO

EXEC sys.sp_cdc_help_change_data_capture
GO

select *
from dbo.users