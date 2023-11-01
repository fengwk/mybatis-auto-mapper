package fun.fengwk.automapper.example.handler;

import fun.fengwk.automapper.example.model.NewExampleDO;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author fengwk
 */
public class ToStringHandler extends BaseTypeHandler<NewExampleDO.Info> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, NewExampleDO.Info parameter, JdbcType jdbcType) throws SQLException {
        String s = parameter.getName() + ":" + parameter.getSort();
        ps.setString(i, s);
    }

    @Override
    public NewExampleDO.Info getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        return buildInfo(columnValue);
    }

    @Override
    public NewExampleDO.Info getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String columnValue = rs.getString(columnIndex);
        return buildInfo(columnValue);
    }

    @Override
    public NewExampleDO.Info getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String columnValue = cs.getString(columnIndex);
        return buildInfo(columnValue);
    }

    private static NewExampleDO.Info buildInfo(String columnValue) {
        if (columnValue != null) {
            String[] s = columnValue.split(":");
            NewExampleDO.Info info = new NewExampleDO.Info();
            info.setName(s[0]);
            info.setSort(Integer.parseInt(s[1]));
            return info;
        } else {
            return null;
        }
    }

}
