#!/usr/bin/env ruby
#
# Pull out the list of options from fulljslint.js.
#

opts = {
  # Hard code, as not mentioned in the source list.
  'predef' => ['The names of predefined global variables', 'StringArray'],
}

# The non-boolean option types.
opt_types = {
  'indent' => 'Integer',
  'maxerr' => 'Integer',
  'maxlen' => 'Integer',
  'predef' => 'StringArray',
}

File.open(ARGV[0]) do |fh|
  # To match an option declaration.
  re = /^\s+'?(\w+)'?\s+(true,?\s(.*)|the.*)/
  while line = fh.gets do
    # The jslint options are now in a comment.  Use the first and last options
    # as delimiters.
    if (line =~ /^\s{4}adsafe\s/) .. (line =~ /^\s{4}widget\s/)
      if md = line.match(re)
        key = md[1]
        if md[3]
          desc = md[3].capitalize
          type = 'Boolean'
        else
          desc = md[2].capitalize
          type = opt_types[key]
        end
        opts[key] = [desc, type]
      else
        raise 'Bad option line "#{line}"'
      end
    end
  end
end

if opts.length == 0
  raise 'Ooops, no options found!'
end

def update_file(f, indent, opts)
  contents = []
  File.open(f) do |fh|
    while line = fh.gets do
      if line =~ /\/\/\s*BEGIN-OPTIONS/
        # Skip up to the end of the options section.
        while line !~ /\/\/\s*END-OPTIONS/
          line = fh.gets
        end

        # And start rewriting.
        contents << "#{indent}// BEGIN-OPTIONS"
        opts.keys.sort.map do |k|
          # key, desc, type
          contents << yield(k, opts[k][0], opts[k][1])
        end
        contents << "#{indent}// END-OPTIONS"
      else
        contents << line.rstrip()
      end
    end
  end
  File.open(f, "w") do |fh|
    fh.write(contents.join("\n") + "\n")
  end
end

indent = "    "
update_file(ARGV[1], indent, opts) do |k,desc,type|
  descEscaped = desc.gsub(/"/, '\\"')
  ["#{indent}/** #{desc} */",
   "#{indent}#{k.upcase}(\"#{descEscaped}\", #{type}.class),",
   ""]
end

update_file(ARGV[2], indent, opts) do |k,desc,type|
  descEscaped = desc.gsub(/"/, '\\"')
  # Set all non-boolean values to "String" so that they can be parsed by
  # us rather than JCommander.
  if type != 'Boolean'
    type = 'String'
  end
  ["#{indent}@Parameter(names = \"--#{k}\", description = \"#{descEscaped}\")",
   "#{indent}public #{type} #{k.upcase} = null;",
   ""]
end
